package dev.hengen.ssaidchangernext.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.LocaleList
import dev.hengen.ssaidchangernext.data.preferences.AppPreferences
import java.util.Locale

object LocaleHelper {

    fun onAttach(context: Context): Context {
        val prefs = AppPreferences(context)
        val lang = prefs.getLanguagePreference()
        return setLocale(context, lang)
    }

    fun getDeviceSystemLocale(): Locale {
        return try {
            Resources.getSystem().configuration.locales.get(0) ?: Locale.getDefault()
        } catch (e: Exception) {
            Locale.getDefault()
        }
    }

    fun setLocale(context: Context, languageCode: String): Context {
        val locale = when (languageCode) {
            "tr" -> Locale("tr")
            "en" -> Locale("en")
            else -> {
                val systemLocale = getDeviceSystemLocale()
                if (systemLocale.language == "tr") Locale("tr") else Locale("en")
            }
        }

        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLocales(LocaleList(locale))

        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        return context.createConfigurationContext(config)
    }
}
