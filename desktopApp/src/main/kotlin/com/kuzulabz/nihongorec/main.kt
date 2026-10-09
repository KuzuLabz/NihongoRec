package com.kuzulabz.nihongorec

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import com.kuzulabz.nihongorec.locale.AppLocale
import nihongorec.shared.generated.resources.Res
import nihongorec.shared.generated.resources.icon

fun main() = application {
    val windowState = rememberWindowState(
        width = 800.dp,
        height = 800.dp
    )

    val defaultLocale = AppLocale.getSystemDefault()
    java.util.Locale.setDefault(java.util.Locale.forLanguageTag(defaultLocale.code))

    Window(
        onCloseRequest = ::exitApplication,
        title = "NihongoRec",
        state = windowState,
        icon = painterResource(Res.drawable.icon),
    ) {
        App()
    }
}