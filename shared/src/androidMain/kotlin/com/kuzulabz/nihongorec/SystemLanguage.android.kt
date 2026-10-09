package com.kuzulabz.nihongorec

import java.util.*

actual fun getSystemLanguageTag(): String {
    return Locale.getDefault().toLanguageTag() // Returns BCP 47 tag, e.g., "en-US"
}