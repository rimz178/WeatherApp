package fi.tuni.weatherapp.locale

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.*
 //This class changes the application's language settings by retrieving the translations from a string file
class Context(base: Context) : ContextWrapper(base) {

    companion object {

        @Suppress("DEPRECATION")
        fun wrap(ctx: Context, language: String): ContextWrapper {
            var context = ctx
            val config = context.resources.configuration
            val sysLocale: Locale =
                getSystemLocale(config)

            if (language != " " && sysLocale.language != language) {
                val locale = Locale(language)
                Locale.setDefault(locale)
                setSystemLocale(config, locale)

            }
            context = context.createConfigurationContext(config)
            return Context(context)
        }

        private fun getSystemLocale(config: Configuration): Locale {
            return config.locales.get(0)
        }

        private fun setSystemLocale(config: Configuration, locale: Locale) {
            config.setLocale(locale)
        }
    }
}