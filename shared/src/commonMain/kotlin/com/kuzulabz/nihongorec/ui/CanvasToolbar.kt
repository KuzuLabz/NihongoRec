package com.kuzulabz.nihongorec.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kuzulabz.nihongorec.icons.clear_all
import com.kuzulabz.nihongorec.icons.undo
import nihongorec.shared.generated.resources.Res
import nihongorec.shared.generated.resources.clear
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DrawingToolbar(
    onClear: () -> Unit,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier,
    hasStrokes: Boolean = false,
) {
    val size = SplitButtonDefaults.SmallContainerHeight

    Row(
        modifier = modifier.height(size),
        horizontalArrangement = Arrangement.spacedBy(SplitButtonDefaults.Spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SplitButtonDefaults.LeadingButton(
            onClick = onClear,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            shapes = SplitButtonDefaults.leadingButtonShapesFor(size),
            contentPadding = SplitButtonDefaults.leadingButtonContentPaddingFor(size),
            enabled = hasStrokes
        ) {
            Icon(
                clear_all,
                modifier = Modifier.size(SplitButtonDefaults.leadingButtonIconSizeFor(size)),
                contentDescription = stringResource(Res.string.clear)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(Res.string.clear))
        }

        SplitButtonDefaults.TrailingButton(
            onClick = onUndo,
            modifier = Modifier.fillMaxHeight(),
            shapes = SplitButtonDefaults.trailingButtonShapesFor(size),
            contentPadding = SplitButtonDefaults.trailingButtonContentPaddingFor(size),
            enabled = hasStrokes
        ) {
            Icon(
                undo,
                modifier = Modifier.size(SplitButtonDefaults.trailingButtonIconSizeFor(size)),
                contentDescription = "Undo"
            )
        }
    }
}