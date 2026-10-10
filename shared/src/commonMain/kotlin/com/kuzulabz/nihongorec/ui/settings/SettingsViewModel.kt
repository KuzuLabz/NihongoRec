package com.kuzulabz.nihongorec.ui.settings

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuzulabz.nihongorec.locale.AppLocale
import com.kuzulabz.nihongorec.locale.LocaleManager
import com.kuzulabz.nihongorec.repository.AppThemeMode
import com.kuzulabz.nihongorec.repository.SettingsRepository
import com.kuzulabz.nihongorec.repository.SettingsState
import com.kuzulabz.nihongorec.repository.ThemeStyle
import com.kuzulabz.nihongorec.utils.generateVibrantRandomColor
import com.materialkolor.PaletteStyle
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository,
    val localeManager: LocaleManager
) : ViewModel() {
    val uiState: StateFlow<SettingsState> = repository.settingsState
        .onEach { state ->
            if (localeManager.supportsInAppSelection) {
                localeManager.applyLocale(state.languageCode)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsState()
        )

    val isLoaded: StateFlow<Boolean> = repository.settingsState
        .map { true }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    init {
        viewModelScope.launch {
            isLoaded.first { it }
            val current = uiState.value
            if (current.randomColorStartup) {
                repository.setSeedColor(generateVibrantRandomColor())
            }
        }
    }

    fun updateThemeMode(themeMode: AppThemeMode) {
        viewModelScope.launch { repository.setThemeMode(themeMode) }
    }

    fun updateThemeStyle(style: ThemeStyle) {
        viewModelScope.launch { repository.setThemeStyle(style) }
    }

    fun setSeedColor(color: Color) {
        viewModelScope.launch { repository.setSeedColor(color) }
    }

    fun toggleAmoled(enabled: Boolean) {
        viewModelScope.launch { repository.setAmoled(enabled) }
    }

    fun setPaletteStyle(paletteStyle: PaletteStyle) {
        viewModelScope.launch { repository.setPaletteStyle(paletteStyle) }
    }

    fun toggleRandomColorStartup(enabled: Boolean) {
        viewModelScope.launch { repository.setRandomColorStartup(enabled) }
    }

    fun toggleGridEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setGridEnabled(enabled) }
    }

    fun onLanguageSelected(locale: AppLocale?) {
        if (locale != null && localeManager.supportsInAppSelection) {
            localeManager.applyLocale(locale)
            viewModelScope.launch {
                repository.setLanguageCode(locale)
            }
        } else {
            localeManager.openSystemSettings()
        }
    }
}