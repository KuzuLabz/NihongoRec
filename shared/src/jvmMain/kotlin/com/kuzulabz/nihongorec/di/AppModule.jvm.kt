package com.kuzulabz.nihongorec.di

import com.kuzulabz.nihongorec.locale.LocaleManager
import com.kuzulabz.nihongorec.locale.PlatformLocaleManager
import org.koin.dsl.module

actual val platformModule = module {
    single<LocaleManager> { PlatformLocaleManager() }
}