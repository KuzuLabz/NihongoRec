package com.kuzulabz.nihongorec.utils

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.window.core.layout.WindowSizeClass
import kotlin.random.Random

fun generateVibrantRandomColor(): Color {
    return Color.hsv(
        hue = Random.nextFloat() * 360f,  // Full spectrum (0° - 360°)
        saturation = 0.7f + Random.nextFloat() * 0.3f, // High saturation (0.7 - 1.0)
        value = 0.8f + Random.nextFloat() * 0.2f       // Bright value (0.8 - 1.0)
    )
}

enum class WindowSize {
    Compact,
    Medium,
    Expanded,
}
@Composable
fun rememberWindowSize(): WindowSize {
    val info = currentWindowAdaptiveInfoV2()
    return when {
        !info.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> WindowSize.Compact
        !info.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> WindowSize.Medium
        else -> WindowSize.Expanded
    }
}