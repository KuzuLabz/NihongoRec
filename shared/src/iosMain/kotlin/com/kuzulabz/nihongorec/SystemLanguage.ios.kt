package com.kuzulabz.nihongorec

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun getSystemLanguageTag(): String {
    // Returns the user's primary preferred language tag (e.g., "en-US", "es-ES")
    return NSLocale.currentLocale.languageCode ?: "en"
}