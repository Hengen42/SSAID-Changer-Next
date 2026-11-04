package dev.hengen.ssaidchangernext

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dev.hengen.ssaidchangernext.data.preferences.AppPreferences
import dev.hengen.ssaidchangernext.ui.screen.HomeScreen
import dev.hengen.ssaidchangernext.ui.theme.SsaidChangerTheme
import dev.hengen.ssaidchangernext.ui.viewmodel.MainViewModel
import dev.hengen.ssaidchangernext.util.LocaleHelper

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = AppPreferences(this)
        LocaleHelper.setLocale(this, prefs.getLanguagePreference())

        setContent {
            SsaidChangerTheme {
                HomeScreen(
                    viewModel = viewModel,
                    onLanguageChanged = {
                        val newLang = prefs.getLanguagePreference()
                        LocaleHelper.setLocale(this@MainActivity, newLang)
                        recreate()
                    }
                )
            }
        }
    }
}
