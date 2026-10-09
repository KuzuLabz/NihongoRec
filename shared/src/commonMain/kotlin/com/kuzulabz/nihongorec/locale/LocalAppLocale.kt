package com.kuzulabz.nihongorec.locale

import com.kuzulabz.nihongorec.getSystemLanguageTag

enum class AppLocale(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    GERMAN("de", "Deutsch"),
    SPANISH("es", "Español"),
    FRENCH("fr", "Français"),
    INDONESIAN("id", "Bahasa Indonesia"),
    ITALIAN("it", "italiano"),
    JAPANESE("ja", "日本語"),
    KOREAN("ko", "한국어"),
    PORTUGUESE("pt", "Português"),
    PORTUGUESE_BRAZIL("pt-br", "Português (Brasil)"),
    RUSSIAN("ru", "Русский язык"),
    VIETNAMESE("vi", "Tiếng Việt"),
    CHINESE_SIMPLIFIED("zh", "简体中文"),
    CHINESE_TRADITIONAL("zh-tw", "繁體中文");

    companion object {
        fun fromCode(code: String?): AppLocale {
            if (code.isNullOrEmpty()) {
                return getSystemDefault()
            }

            // Normalizes BCP 47 tags like "pt_BR" or "pt-br" to "pt-BR"
            val normalizedTag = code.replace("_", "-")

            // 1. Exact match (e.g. "pt-BR", "zh-TW", "en")
            entries.find { it.code.equals(normalizedTag, ignoreCase = true) }?.let { return it }

            // 2. Primary language match (e.g. "pt-PT" -> matches "pt")
            val primaryLang = normalizedTag.substringBefore("-")
            entries.find { it.code.equals(primaryLang, ignoreCase = true) }?.let { return it }

            // 3. Fallback to English if system/code is unsupported
            return ENGLISH
        }

        fun getSystemDefault(): AppLocale {
            val systemTag = getSystemLanguageTag() // Uses your expect fun getSystemLanguageTag()
            return fromCode(systemTag)
        }
    }
}