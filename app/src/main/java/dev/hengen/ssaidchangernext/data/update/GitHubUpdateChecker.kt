package dev.hengen.ssaidchangernext.data.update

import dev.hengen.ssaidchangernext.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

data class AppRelease(val version: String, val pageUrl: String)

object GitHubUpdateChecker {
    private const val REPOSITORY = "Hengen42/SSAID-Changer-Next"

    suspend fun findUpdate(currentVersion: String): AppRelease? = withContext(Dispatchers.IO) {
        val connection = URL("https://api.github.com/repos/$REPOSITORY/releases/latest")
            .openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
            connection.setRequestProperty("User-Agent", "SSAID-Changer-Next/${BuildConfig.VERSION_NAME}")
            when (connection.responseCode) {
                HttpURLConnection.HTTP_NOT_FOUND -> null // No published stable release yet.
                HttpURLConnection.HTTP_OK -> connection.inputStream.bufferedReader().use {
                    parseRelease(it.readText(), currentVersion)
                }
                else -> throw IOException("GitHub returned HTTP ${connection.responseCode}")
            }
        } finally {
            connection.disconnect()
        }
    }

    internal fun parseRelease(json: String, currentVersion: String): AppRelease? {
        val release = JSONObject(json)
        if (release.optBoolean("draft") || release.optBoolean("prerelease")) return null
        val tag = release.getString("tag_name")
        val remote = versionParts(tag) ?: throw IOException("Unsupported release version")
        val current = versionParts(currentVersion) ?: throw IOException("Unsupported installed version")
        val comparison = (0 until maxOf(remote.size, current.size))
            .asSequence()
            .map { (remote.getOrElse(it) { 0L }).compareTo(current.getOrElse(it) { 0L }) }
            .firstOrNull { it != 0 } ?: 0
        if (comparison <= 0) return null

        val pageUrl = release.getString("html_url")
        val uri = URI(pageUrl)
        if (uri.scheme != "https" || uri.host != "github.com" || uri.userInfo != null ||
            uri.port != -1 || !uri.path.startsWith("/$REPOSITORY/releases/tag/")
        ) throw IOException("Unexpected release URL")
        return AppRelease(tag.removePrefix("v").removePrefix("V"), pageUrl)
    }

    private fun versionParts(version: String): List<Long>? {
        val normalized = version.trim().removePrefix("v").removePrefix("V")
        if (!normalized.matches(Regex("[0-9]+(\\.[0-9]+)*"))) return null
        return normalized.split('.').map { it.toLongOrNull() ?: return null }
    }
}
