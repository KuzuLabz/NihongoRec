package com.kuzulabz.nihongorec.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformScrollbar(modifier: Modifier, scrollState: ScrollState) {
    VerticalScrollbar(
        modifier = modifier
            .fillMaxHeight(),
        adapter = rememberScrollbarAdapter(scrollState),
    )
}