package com.kuzulabz.nihongorec.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kuzulabz.nihongorec.Platform
import com.kuzulabz.nihongorec.getPlatform
import com.kuzulabz.nihongorec.icons.settings
import com.kuzulabz.nihongorec.ml.Category
import com.kuzulabz.nihongorec.repository.ThemeStyle
import com.kuzulabz.nihongorec.ui.*
import com.kuzulabz.nihongorec.ui.settings.SettingsViewModel
import com.kuzulabz.nihongorec.utils.WindowSize
import com.kuzulabz.nihongorec.utils.rememberWindowSize
import dev.ggoggam.inklet.material3.inkletSurface
import kotlinx.coroutines.launch
import nihongorec.shared.generated.resources.Res
import nihongorec.shared.generated.resources.copy_message
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(viewModel: CanvasViewModel = koinViewModel(), onOpenSettings: () -> Unit) {

    val windowSizeClass = rememberWindowSize()
    val isCompact = windowSizeClass === WindowSize.Compact

    val state = viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val focusRequester = remember { FocusRequester() }

    val onPredictionClick: (Category) -> Unit = { category ->
        if (getPlatform() === Platform.DESKTOP) scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                message = getString(Res.string.copy_message, category.label),
                duration = SnackbarDuration.Short,
                withDismissAction = false
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "日本語Rec",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .focusRequester(focusRequester)
                .focusable()
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                    when {
                        // Undo
                        (event.isCtrlPressed || event.isMetaPressed) &&
                                event.key == Key.Z &&
                                !event.isShiftPressed -> {
                            viewModel.undo()
                            true
                        }

                        // Clear
                        (event.isCtrlPressed || event.isMetaPressed) &&
                                (event.key == Key.Delete || event.key == Key.Backspace) -> {
                            viewModel.clearCanvas()
                            true
                        }

                        else -> false
                    }
                }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type == PointerEventType.Press) {
                                focusRequester.requestFocus()
                            }
                        }
                    }
                }
        ) {
            if (isCompact) {
                // MOBILE
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalPredictionsView(
                        predictions = state.value.predictions,
                        onPredictionClick = onPredictionClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    CanvasSection(
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.4f)
                    )
                }
            } else {
                // TABLET / DESKTOP
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CanvasSection(
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxHeight().padding(horizontal = 16.dp)
                    )

                    VerticalPredictionsView(
                        predictions = state.value.predictions,
                        onPredictionClick = onPredictionClick,
                        modifier = Modifier.width(180.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CanvasSection(
    viewModel: CanvasViewModel,
    modifier: Modifier = Modifier,
    settingsModel: SettingsViewModel = koinViewModel(),
) {
    val state by settingsModel.uiState.collectAsStateWithLifecycle()
    val isInk = state.themeStyle == ThemeStyle.INK
    val windowSizeClass = rememberWindowSize()
    val isCompact = windowSizeClass === WindowSize.Compact

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .aspectRatio(1f, matchHeightConstraintsFirst = !isCompact)
                    .clip(RoundedCornerShape(16.dp))
                    .then(
                        if (isInk)
                            Modifier
                        else
                        Modifier.border(
                            2.dp,
                            MaterialTheme.colorScheme.outlineVariant,
                            RoundedCornerShape(16.dp)
                        )
                    )
                    .then(if (isInk) Modifier.inkletSurface() else Modifier),
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                tonalElevation = 2.dp
            ) {
                val gridColor = MaterialTheme.colorScheme.outline
                BoxWithConstraints(
                    modifier = Modifier.drawBehind {
                        if (state.gridEnabled) {
                            val strokeWidth = 1.dp.toPx()

                            drawLine(
                                color = gridColor,
                                start = Offset(size.width / 2, 0f),
                                end = Offset(size.width / 2, size.height),
                                strokeWidth = strokeWidth,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, size.height / 2),
                                end = Offset(size.width, size.height / 2),
                                strokeWidth = strokeWidth,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        }
                    }
                ) {
                    val width = constraints.maxWidth
                    val height = constraints.maxHeight
                    val currentDensity = LocalDensity.current
                    val themeColor = MaterialTheme.colorScheme.primary

                    SideEffect {
                        viewModel.initializeBuffer(width, height, currentDensity)
                    }

                    LaunchedEffect(width, height) {
                        if (width > 0 && height > 0) {
                            viewModel.initializeBuffer(width, height, currentDensity)
                        }
                    }

                    LaunchedEffect(themeColor) {
                        viewModel.updateThemeColor(themeColor)
                    }

                    DrawPad(
                        width = width,
                        height = height,
                        viewModel = viewModel
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            DrawingToolbar(
                onClear = {
                    viewModel.clearCanvas()
                },
                onUndo = { viewModel.undo() },
                modifier = Modifier.fillMaxWidth(),
                hasStrokes = viewModel.strokes.isNotEmpty()
            )
        }
    }
}