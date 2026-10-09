package com.kuzulabz.nihongorec

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun SetStatusBarTheme(darkTheme: Boolean) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity ?: return

    DisposableEffect(darkTheme) {
        val barStyle = if (darkTheme) {
            // Dark mode: transparent background with light status bar icons
            SystemBarStyle.dark(Color.Transparent.toArgb())
        } else {
            // Light mode: transparent background with dark status bar icons
            SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
        }

        activity.enableEdgeToEdge(
            statusBarStyle = barStyle,
            navigationBarStyle = barStyle
        )

        onDispose { }
    }
}