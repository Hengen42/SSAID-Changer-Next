package dev.hengen.ssaidchangernext.engine

import com.topjohnwu.superuser.Shell
import dev.hengen.ssaidchangernext.data.model.SsaidResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.SecureRandom

object SsaidEngine {

    private const val TMP_DIR = "/data/local/tmp/ssaidchanger"
    private const val HEX_CHARS = "0123456789abcdef"

    fun generateRandomSsaid(): String {
        val random = SecureRandom()
        val sb = StringBuilder(16)
        for (i in 0 until 16) {
            sb.append(HEX_CHARS[random.nextInt(HEX_CHARS.length)])
        }
        return sb.toString()
    }

    suspend fun isRootAvailable(forceFresh: Boolean = false): Boolean = withContext(Dispatchers.IO) {
        try {
            if (forceFresh) {
                try {
                    Shell.getCachedShell()?.close()
                } catch (e: Exception) {
                    // ignore
                }
            }
            val result = Shell.cmd("id").exec()
            result.isSuccess && result.out.any { it.contains("uid=0") }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Reads the current SSAID for a single package directly from the XML/ABX.
     */
    suspend fun readSsaid(packageName: String, userId: Int = 0): String? = withContext(Dispatchers.IO) {
        val ssaidPath = "/data/system/users/$userId/settings_ssaid.xml"

        val cmd = """
            if [ ! -f "$ssaidPath" ]; then
                exit 1
            fi

            if command -v abx2xml >/dev/null 2>&1; then
                abx2xml "$ssaidPath" - 2>/dev/null | grep -F 'package="$packageName"' || true
            else
                grep -F 'package="$packageName"' "$ssaidPath" || true
            fi
        """.trimIndent()

        val result = Shell.cmd(cmd).exec()
        val regexValue = Regex("""value="([0-9a-fA-F]+)"""")
        val regexDefault = Regex("""defaultValue="([0-9a-fA-F]+)"""")

        for (line in result.out) {
            if (line.contains("package=\"$packageName\"")) {
                val match = regexValue.find(line) ?: regexDefault.find(line)
                if (match != null) {
                    return@withContext match.groupValues[1]
                }
            }
        }
        null
    }

    /**
     * Updates or Inserts the SSAID for a package.
     */
    suspend fun updateSsaid(
        packageName: String,
        newSsaid: String,
        userId: Int = 0,
        stopApp: Boolean = true
    ): SsaidResult = withContext(Dispatchers.IO) {
        val cleanId = newSsaid.trim().lowercase()
        if (!cleanId.matches(Regex("^[0-9a-f]{16}$"))) {
            return@withContext SsaidResult.Error("SSAID must be exactly 16 hexadecimal characters (0-9, a-f).")
        }

        val ssaidPath = "/data/system/users/$userId/settings_ssaid.xml"

        val script = """
            mkdir -p $TMP_DIR
            chmod 700 $TMP_DIR

            IN="$ssaidPath"
            TMP="$TMP_DIR/working.xml"
            OUT="$TMP_DIR/working_out.xml"
            BAK="${'$'}IN.ssaidchanger.bak"

            if [ ! -f "${'$'}IN" ]; then
                echo "ERR_FILE_NOT_FOUND"
                exit 1
            fi

            # Backup original file on first run if not already backed up
            if [ ! -f "${'$'}BAK" ]; then
                cp -p "${'$'}IN" "${'$'}BAK"
            fi

            ABX_MODE=0
            if command -v abx2xml >/dev/null 2>&1; then
                if abx2xml "${'$'}IN" "${'$'}TMP" >/dev/null 2>&1; then
                    ABX_MODE=1
                else
                    cat "${'$'}IN" > "${'$'}TMP"
                fi
            else
                cat "${'$'}IN" > "${'$'}TMP"
            fi

            PKG="$packageName"
            VAL="$cleanId"

            # Check if package exists in the XML
            if grep -q "package=\"${'$'}PKG\"" "${'$'}TMP"; then
                # Update existing entry
                awk -v pkg="${'$'}PKG" -v val="${'$'}VAL" '
                index(${'$'}0, "package=\"" pkg "\"") {
                    gsub(/value="[^"]*"/, "value=\"" val "\"")
                    gsub(/defaultValue="[^"]*"/, "defaultValue=\"" val "\"")
                }
                { print }
                ' "${'$'}TMP" > "${'$'}OUT"
            else
                # Entry does not exist -> Insert new entry before </settings> or at the end
                MAX_ID=${'$'}(grep -o 'id="[0-9]*"' "${'$'}TMP" | grep -o '[0-9]*' | sort -n | tail -1)
                [ -z "${'$'}MAX_ID" ] && MAX_ID=100 || MAX_ID=${'$'}((MAX_ID + 1))

                NEW_LINE="  <setting id=\"${'$'}MAX_ID\" name=\"${'$'}PKG\" value=\"${'$'}VAL\" package=\"${'$'}PKG\" defaultValue=\"${'$'}VAL\" defaultSysSet=\"false\" tag=\"null\" />"

                if grep -q "</settings>" "${'$'}TMP"; then
                    sed "/<\/settings>/i\\
${'$'}NEW_LINE" "${'$'}TMP" > "${'$'}OUT"
                else
                    cat "${'$'}TMP" > "${'$'}OUT"
                    echo "${'$'}NEW_LINE" >> "${'$'}OUT"
                fi
            fi

            # Convert back to ABX if originally ABX
            if [ "${'$'}ABX_MODE" -eq 1 ]; then
                if command -v xml2abx >/dev/null 2>&1; then
                    if ! xml2abx "${'$'}OUT" "${'$'}IN" >/dev/null 2>&1; then
                        echo "ERR_XML2ABX_FAILED"
                        exit 2
                    fi
                else
                    cat "${'$'}OUT" > "${'$'}IN"
                fi
            else
                cat "${'$'}OUT" > "${'$'}IN"
            fi

            # Fix ownership and permissions
            chown system:system "${'$'}IN"
            chmod 600 "${'$'}IN"
            restorecon "${'$'}IN" 2>/dev/null || true
            sync

            # Clean up working files
            rm -f "${'$'}TMP" "${'$'}OUT"

            echo "SUCCESS"
        """.trimIndent()

        val output = Shell.cmd(script).exec()

        if (output.out.any { it.contains("SUCCESS") }) {
            if (stopApp) {
                Shell.cmd("am force-stop $packageName 2>/dev/null || true").exec()
            }
            SsaidResult.Success(cleanId, "SSAID successfully updated to $cleanId")
        } else {
            val err = output.out.joinToString("\n").ifBlank {
                output.err.joinToString("\n").ifBlank { "Unknown shell error" }
            }
            SsaidResult.Error("Failed to update SSAID: $err")
        }
    }

    /**
     * Checks if a full backup file exists.
     */
    suspend fun hasFullBackup(userId: Int = 0): Boolean = withContext(Dispatchers.IO) {
        val bak = "/data/system/users/$userId/settings_ssaid.xml.ssaidchanger.bak"
        val res = Shell.cmd("[ -f \"$bak\" ] && echo YES || echo NO").exec()
        res.out.any { it.contains("YES") }
    }

    /**
     * Restores the entire settings_ssaid.xml from original full backup.
     */
    suspend fun restoreFullBackup(userId: Int = 0): SsaidResult = withContext(Dispatchers.IO) {
        val inPath = "/data/system/users/$userId/settings_ssaid.xml"
        val bakPath = "$inPath.ssaidchanger.bak"

        val script = """
            if [ ! -f "$bakPath" ]; then
                echo "ERR_NO_BACKUP"
                exit 1
            fi

            cp -p "$bakPath" "$inPath"
            chown system:system "$inPath"
            chmod 600 "$inPath"
            restorecon "$inPath" 2>/dev/null || true
            sync
            echo "RESTORE_SUCCESS"
        """.trimIndent()

        val output = Shell.cmd(script).exec()
        if (output.out.any { it.contains("RESTORE_SUCCESS") }) {
            SsaidResult.Success("FULL_RESTORE", "Full system SSAID database restored from backup.")
        } else {
            SsaidResult.Error("Failed to restore full backup: ${output.err.joinToString("\n")}")
        }
    }

    suspend fun clearAppData(packageName: String): Boolean = withContext(Dispatchers.IO) {
        val result = Shell.cmd("pm clear $packageName").exec()
        result.isSuccess
    }

    suspend fun softReboot(): Boolean = withContext(Dispatchers.IO) {
        val result = Shell.cmd("kill -9 $(pidof system_server) || setprop ctl.restart zygote").exec()
        result.isSuccess
    }

    suspend fun rebootDevice(): Boolean = withContext(Dispatchers.IO) {
        val result = Shell.cmd("/system/bin/reboot || svc power reboot || reboot").exec()
        result.isSuccess
    }
}
