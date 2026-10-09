package com.kuzulabz.nihongorec.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kuzulabz.nihongorec.SetStatusBarTheme
import com.kuzulabz.nihongorec.repository.AppThemeMode
import com.kuzulabz.nihongorec.repository.ThemeStyle
import com.kuzulabz.nihongorec.ui.settings.SettingsViewModel
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.material3.DynamicMaterialTheme
import dev.ggoggam.inklet.InkletStyle
import dev.ggoggam.inklet.InkletTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppTheme(
    settingsModel: SettingsViewModel = koinViewModel(),
    content: @Composable () -> Unit
) {
    val state by settingsModel.uiState.collectAsStateWithLifecycle()
    val isLoaded by settingsModel.isLoaded.collectAsStateWithLifecycle()

    if (!isLoaded) {
        val isSystemDark = isSystemInDarkTheme()
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = if (isSystemDark) Color(0xFF121212) else Color.White
        ) {}
        return
    }

    val isDark = when (state.themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    SetStatusBarTheme(isDark)

    if (state.themeStyle == ThemeStyle.MATERIAL) {
        DynamicMaterialTheme(
            seedColor = state.seedColor,
            isDark = isDark,
            animate = true,
            content = content,
            isAmoled = state.isAmoled,
            style = state.paletteStyle,
            specVersion = ColorSpec.SpecVersion.SPEC_2026
        )
    } else {
        DynamicMaterialTheme(
            seedColor = state.seedColor,
            isDark = isDark,
            animate = true,
            isAmoled = state.isAmoled,
            style = state.paletteStyle,
            specVersion = ColorSpec.SpecVersion.SPEC_2026
        ) {
            InkletTheme(style = InkletStyle(roughness = 0.8, boil = 0.5, animate = true), reduceMotion = false, content = content)
        }
    }
}