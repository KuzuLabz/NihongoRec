package com.kuzulabz.nihongorec.di

import com.kuzulabz.nihongorec.datastore.createDataStore
import com.kuzulabz.nihongorec.datastore.getDataStorePath
import com.kuzulabz.nihongorec.ml.CharClassifierOnnx
import com.kuzulabz.nihongorec.repository.SettingsRepository
import com.kuzulabz.nihongorec.ui.CanvasViewModel
import com.kuzulabz.nihongorec.ui.settings.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val platformModule: Module

val appModule = module {
    single { CharClassifierOnnx() }

    single { createDataStore(producePath = { getDataStorePath() }) }

    singleOf(::SettingsRepository)

    // ViewModels scoped to screens/navigation
    viewModelOf(::CanvasViewModel)
    viewModelOf(::SettingsViewModel)
}