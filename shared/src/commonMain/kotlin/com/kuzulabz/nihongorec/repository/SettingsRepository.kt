package com.kuzulabz.nihongorec.repository

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.kuzulabz.nihongorec.locale.AppLocale
import com.materialkolor.PaletteStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nihongorec.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource


val DEFAULT_SEED_COLOR = Color(0xFF34E716)

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        @Composable
        fun getTitle(mode: AppThemeMode) = when (mode) {
            SYSTEM -> stringResource(Res.string.system)
            DARK -> stringResource(Res.string.dark)
            LIGHT -> stringResource(Res.string.light)
        }
    }
}

enum class ThemeStyle {
    MATERIAL,
    INK;

    companion object {
        @Composable
        fun getTitle(mode: ThemeStyle) = when (mode) {
            MATERIAL -> stringResource(Res.string.material)
            INK -> stringResource(Res.string.ink)
        }
    }
}

data class SettingsState(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val themeStyle: ThemeStyle = ThemeStyle.MATERIAL,
    val seedColor: Color = Color(0xFF34E716),
    val isAmoled: Boolean = false,
    val paletteStyle: PaletteStyle = PaletteStyle.Expressive,
    val randomColorStartup: Boolean = false,
    val gridEnabled: Boolean = true,

    // locale
    val languageCode: AppLocale = AppLocale.ENGLISH,
)

class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    private object AppSettingsKeys {
        // Theme
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val THEME_STYLE = stringPreferencesKey("theme_style")
        val SEED_COLOR = intPreferencesKey("seed_color")
        val IS_AMOLED = booleanPreferencesKey("is_amoled")
        val PALETTE_STYLE = stringPreferencesKey("palette_style")
        val RANDOM_COLOR_STARTUP = booleanPreferencesKey("random_color_startup")

        // App
        val GRID_ENABLED = booleanPreferencesKey("grid_enabled")
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
    }

    val settingsState: Flow<SettingsState> = dataStore.data.map { prefs ->
        val rawMode = prefs[AppSettingsKeys.THEME_MODE] ?: AppThemeMode.SYSTEM.name
        val rawStyle = prefs[AppSettingsKeys.THEME_STYLE] ?: ThemeStyle.MATERIAL.name
        val colorInt = prefs[AppSettingsKeys.SEED_COLOR]
        val code = prefs[AppSettingsKeys.LANGUAGE_CODE]

        val diskSeedColor = if (colorInt != null) Color(colorInt) else DEFAULT_SEED_COLOR

        SettingsState(
            themeMode = runCatching { AppThemeMode.valueOf(rawMode) }.getOrDefault(AppThemeMode.SYSTEM),
            themeStyle = runCatching { ThemeStyle.valueOf(rawStyle) }.getOrDefault(ThemeStyle.MATERIAL),
            seedColor = diskSeedColor,
            isAmoled = prefs[AppSettingsKeys.IS_AMOLED] ?: false,
            paletteStyle = PaletteStyle.fromName(prefs[AppSettingsKeys.PALETTE_STYLE] ?: "Expressive"),
            randomColorStartup = prefs[AppSettingsKeys.RANDOM_COLOR_STARTUP] ?: false,
            gridEnabled = prefs[AppSettingsKeys.GRID_ENABLED] ?: true,
            languageCode = AppLocale.fromCode(code)
        )
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        dataStore.edit { prefs ->
            prefs[AppSettingsKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun setThemeStyle(style: ThemeStyle) {
        dataStore.edit { prefs ->
            prefs[AppSettingsKeys.THEME_STYLE] = style.name
        }
    }

    suspend fun setSeedColor(color: Color) {
        dataStore.edit { prefs ->
            prefs[AppSettingsKeys.SEED_COLOR] = color.toArgb()
        }
    }

    suspend fun setAmoled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[AppSettingsKeys.IS_AMOLED] = enabled
        }
    }

    suspend fun setPaletteStyle(paletteStyle: PaletteStyle) {
        dataStore.edit { prefs ->
            prefs[AppSettingsKeys.PALETTE_STYLE] = paletteStyle.name
        }
    }

    suspend fun setRandomColorStartup(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[AppSettingsKeys.RANDOM_COLOR_STARTUP] = enabled
        }
    }

    suspend fun setGridEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[AppSettingsKeys.GRID_ENABLED] = enabled }
    }

    suspend fun setLanguageCode(locale: AppLocale) {
        dataStore.edit { prefs -> prefs[AppSettingsKeys.LANGUAGE_CODE] = locale.code }
    }
}