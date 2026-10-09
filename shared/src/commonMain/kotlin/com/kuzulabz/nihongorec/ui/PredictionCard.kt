package com.kuzulabz.nihongorec.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kuzulabz.nihongorec.icons.content_copy
import com.kuzulabz.nihongorec.ml.Category
import com.kuzulabz.nihongorec.repository.ThemeStyle
import com.kuzulabz.nihongorec.ui.settings.SettingsViewModel
import dev.ggoggam.inklet.material3.inkletSurface
import nihongorec.shared.generated.resources.NotoSansJP_Regular_subset
import nihongorec.shared.generated.resources.Res
import nihongorec.shared.generated.resources.content_copy
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PredictionCard(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    settingsModel: SettingsViewModel = koinViewModel(),
) {
    val state by settingsModel.uiState.collectAsStateWithLifecycle()
    val isInk = state.themeStyle == ThemeStyle.INK
    val haptic = LocalHapticFeedback.current
    val hasContent = category.label.isNotEmpty()

    Surface(
        onClick = {
            onClick()
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        },
        modifier = modifier.then(if (isInk) Modifier.inkletSurface() else Modifier),
        shape = RoundedCornerShape(12.dp),
        color = if (isInk) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerHigh,
        border = if (isInk) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        enabled = category.label.isNotEmpty(),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = category.label,
                    fontFamily = FontFamily(Font(Res.font.NotoSansJP_Regular_subset, weight = FontWeight.Normal)),
                    style = MaterialTheme.typography.titleLargeEmphasized
                )
                Text(
                    text = "${(category.score * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.visible(category.score > 0f)
                )
            }
            if (hasContent) {
                Icon(
                    imageVector = content_copy,
                    contentDescription = stringResource(Res.string.content_copy),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(12.dp)
                )
            }
        }
    }
}

@Composable
fun VerticalPredictionsView(
    predictions: List<Category>,
    onPredictionClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxHeight().fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(predictions) { category ->
            PredictionCard(
                category = category,
                onClick = {
                    clipboardManager.setText(AnnotatedString(category.label))
                    onPredictionClick(category)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 96.dp)
                    .aspectRatio(1f)
            )
        }
    }
}

@Composable
fun HorizontalPredictionsView(
    predictions: List<Category>,
    onPredictionClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val itemsToDisplay = remember(predictions) { predictions.take(9) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Break 9 items into 3 chunks of 3 for the rows
        itemsToDisplay.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Each row gets exactly 1/3 of total height
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { category ->
                    PredictionCard(
                        category = category,
                        onClick = {
                            if (category.label.isNotEmpty()) {
                                clipboardManager.setText(AnnotatedString(category.label))
                                onPredictionClick(category)
                            }
                        },
                        modifier = Modifier
                            .weight(1f) // Each card gets 1/3 of row width
                            .fillMaxHeight()
                    )
                }

                // Fill remaining empty slots in incomplete rows (if predictions < 9)
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}