package com.kuzulabz.nihongorec.locale

interface LocaleManager {
    /** Indicates whether the UI should show an in-app language picker */
    val supportsInAppSelection: Boolean

    /** Opens OS app settings (Mobile) */
    fun openSystemSettings()

    /** Applies locale configuration at runtime (Desktop) */
    fun applyLocale(locale: AppLocale)
}

expect class PlatformLocaleManager(context: Any? = null) : LocaleManager