package dev.hengen.ssaidchangernext.data.preferences

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveOriginalSsaid(packageName: String, originalId: String) {
        val key = KEY_ORIGINAL_PREFIX + packageName
        if (!prefs.contains(key) && originalId.isNotEmpty()) {
            prefs.edit().putString(key, originalId).apply()
        }
    }

    fun getOriginalSsaid(packageName: String): String? {
        return prefs.getString(KEY_ORIGINAL_PREFIX + packageName, null)
    }

    fun removeOriginalSsaid(packageName: String) {
        prefs.edit().remove(KEY_ORIGINAL_PREFIX + packageName).apply()
    }

    fun getAllModifiedPackages(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for ((key, value) in prefs.all) {
            if (key.startsWith(KEY_ORIGINAL_PREFIX) && value is String) {
                val pkg = key.removePrefix(KEY_ORIGINAL_PREFIX)
                map[pkg] = value
            }
        }
        return map
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    fun getLanguagePreference(): String {
        return prefs.getString(KEY_LANGUAGE, "auto") ?: "auto"
    }

    fun setLanguagePreference(languageCode: String) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    companion object {
        private const val PREFS_NAME = "ssaid_changer_next_prefs"
        private const val KEY_ORIGINAL_PREFIX = "orig_ssaid_"
        private const val KEY_LANGUAGE = "app_language_pref"
    }
}
