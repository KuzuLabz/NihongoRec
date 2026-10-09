package com.kuzulabz.nihongorec.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ScrollBar(modifier: Modifier = Modifier, scrollState: ScrollState) {
    PlatformScrollbar(modifier, scrollState)
}

@Composable
expect fun PlatformScrollbar(modifier: Modifier = Modifier, scrollState: ScrollState)