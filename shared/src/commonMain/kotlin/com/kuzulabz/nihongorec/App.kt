package com.kuzulabz.nihongorec

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.kuzulabz.nihongorec.di.appModule
import com.kuzulabz.nihongorec.di.platformModule
import com.kuzulabz.nihongorec.ui.screens.MainScreen
import com.kuzulabz.nihongorec.ui.screens.SettingsScreen
import com.kuzulabz.nihongorec.ui.theme.AppTheme
import kotlinx.serialization.Serializable
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Serializable
sealed interface AppRoute : NavKey {
    @Serializable
    data object Main : AppRoute

    @Serializable
    data object Settings : AppRoute
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
@Preview
fun App() {
    val backStack = remember { mutableStateListOf<AppRoute>(AppRoute.Main) }

    KoinApplication(
        configuration = koinConfiguration {
            modules(appModule, platformModule)
        }
    ) {
        LocaleProvider {
            AppTheme {
                NavDisplay(
                    backStack = backStack,
                    onBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
                    entryProvider = entryProvider {
                        entry<AppRoute.Main> {
                            MainScreen(
                                onOpenSettings = { backStack.add(AppRoute.Settings) }
                            )
                        }

                        entry<AppRoute.Settings> {
                            SettingsScreen(
                                onClose = { backStack.removeAt(backStack.lastIndex) }
                            )
                        }
                    }
                )
            }
        }
    }
}