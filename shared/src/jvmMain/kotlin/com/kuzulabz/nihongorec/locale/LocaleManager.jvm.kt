package com.kuzulabz.nihongorec.locale

import java.util.*

actual class PlatformLocaleManager actual constructor(context: Any?) : LocaleManager {
    override val supportsInAppSelection: Boolean
        get() = true

    override fun openSystemSettings() {
        // Not supported
    }

    override fun applyLocale(locale: AppLocale) {
        val javaLocale = Locale.forLanguageTag(locale.code)
        Locale.setDefault(javaLocale)
    }
}