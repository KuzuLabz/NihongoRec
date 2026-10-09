package com.kuzulabz.nihongorec

import androidx.compose.runtime.*
import com.kuzulabz.nihongorec.locale.AppLocale
import com.kuzulabz.nihongorec.ui.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

val LocalAppLocale = staticCompositionLocalOf { AppLocale.ENGLISH }

@Composable
fun LocaleProvider(
    settingsModel: SettingsViewModel = koinViewModel(),
    content: @Composable () -> Unit
) {
    val uiState by settingsModel.uiState.collectAsState()

    CompositionLocalProvider(
        LocalAppLocale provides uiState.languageCode
    ) {
        content()
    }
}