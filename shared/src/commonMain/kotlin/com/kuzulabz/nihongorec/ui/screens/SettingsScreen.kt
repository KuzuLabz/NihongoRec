package com.kuzulabz.nihongorec.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kuzulabz.nihongorec.*
import com.kuzulabz.nihongorec.icons.arrow_back
import com.kuzulabz.nihongorec.icons.keyboard_arrow_down
import com.kuzulabz.nihongorec.icons.open_in_browser
import com.kuzulabz.nihongorec.icons.shuffle
import com.kuzulabz.nihongorec.repository.AppThemeMode
import com.kuzulabz.nihongorec.repository.ThemeStyle
import com.kuzulabz.nihongorec.ui.ListItemSection
import com.kuzulabz.nihongorec.ui.ScrollBar
import com.kuzulabz.nihongorec.ui.settings.LocaleListItem
import com.kuzulabz.nihongorec.ui.settings.SettingsViewModel
import com.kuzulabz.nihongorec.utils.generateVibrantRandomColor
import com.materialkolor.PaletteStyle
import dev.ggoggam.inklet.material3.InkletRadioButton
import dev.ggoggam.inklet.material3.InkletToggle
import dev.ggoggam.inklet.material3.inkletBorder
import nihongorec.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

var availablePalettes = listOf(PaletteStyle.Vibrant, PaletteStyle.Expressive, PaletteStyle.TonalSpot, PaletteStyle.Neutral)

@Composable
fun OpenBrowserIcon() {
    Icon(open_in_browser, contentDescription = stringResource(Res.string.open_in_browser))
}

@Composable
fun ThemedSwitch(isInk: Boolean, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    if (isInk) {
        InkletToggle(checked, onCheckedChange = onCheckedChange)
    } else {
        Switch(checked, onCheckedChange)
    }
}

@Composable
fun ThemedRadioButton(isInk: Boolean, selected: Boolean, onClick: () -> Unit) {
    if (isInk) {
        InkletRadioButton(selected, onClick)
    } else {
        RadioButton(selected, onClick)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(onClose: () -> Unit, settingsModel: SettingsViewModel = koinViewModel()) {
    val uriHandler = LocalUriHandler.current
    val state by settingsModel.uiState.collectAsStateWithLifecycle()
    val isInk = state.themeStyle === ThemeStyle.INK
    var expanded by rememberSaveable() { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "chevronRotation"
    )

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = arrow_back,
                            contentDescription = stringResource(Res.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp), // Spacing between main sections
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .verticalScroll(scrollState)
            ) {
                ListItemSection(stringResource(Res.string.general)) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
                    ) {
                        SegmentedListItem(
                            content = { Text(text = stringResource(Res.string.show_grid)) },
                            onClick = { settingsModel.toggleGridEnabled(!state.gridEnabled) },
                            trailingContent = {
                                ThemedSwitch(
                                    isInk = isInk,
                                    checked = state.gridEnabled,
                                    onCheckedChange = { settingsModel.toggleGridEnabled(it) })
                            },
                            shapes = ListItemDefaults.segmentedShapes(0, 2),
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            //                enabled = isDarkMode
                        )
                        LocaleListItem(
                            state.languageCode,
                            settingsModel.localeManager.supportsInAppSelection,
                            onLocaleSelected = { selectedLocale -> settingsModel.onLanguageSelected(selectedLocale) },
                        )
                    }
                }
                ListItemSection(stringResource(Res.string.theme)) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
                    ) {
                        SegmentedListItem(
                            content = { Text(text = stringResource(Res.string.color_scheme)) },
                            shapes = ListItemDefaults.segmentedShapes(0, 6),
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            supportingContent = {
                                FlowRow(
                                    Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    AppThemeMode.entries.forEachIndexed { index, mode ->
                                        val shape = when (index) {
                                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                            AppThemeMode.entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                        }
                                        ToggleButton(
                                            checked = state.themeMode == mode,
                                            onCheckedChange = { settingsModel.updateThemeMode(mode) },
                                            colors = ToggleButtonDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                                            shapes = shape,
                                            modifier = Modifier.weight(1f).semantics() { role = Role.RadioButton }.then(if (isInk && state.themeMode == mode) Modifier.inkletBorder(cornerRadius = 20.dp) else Modifier),
                                        ) {
                                            Text(AppThemeMode.getTitle(mode))
                                        }
                                    }
                                }
                            }
                        )
                        SegmentedListItem(
                            content = { Text(text = stringResource(Res.string.theme_style)) },
                            shapes = ListItemDefaults.segmentedShapes(1, 6),
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            supportingContent = {
                                FlowRow(
                                    Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    ThemeStyle.entries.forEachIndexed { index, mode ->
                                        ToggleButton(
                                            checked = state.themeStyle == mode,
                                            onCheckedChange = { settingsModel.updateThemeStyle(mode) },
                                            shapes =
                                                when (index) {
                                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                                    ThemeStyle.entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                                },
                                            colors = ToggleButtonDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh, ),
                                            modifier = Modifier.weight(1f).semantics() { role = Role.RadioButton }.then(if (isInk && state.themeStyle == mode) Modifier.inkletBorder(cornerRadius = 20.dp) else Modifier),
                                        ) {
                                            Text(ThemeStyle.getTitle(mode))
                                        }
                                    }
                                }
                            }
                        )
                        SegmentedListItem(
                            content = { Text(text = stringResource(Res.string.theme_color)) },
                            shapes = ListItemDefaults.segmentedShapes(2, 6),
                            onClick = { settingsModel.setSeedColor(generateVibrantRandomColor()) },
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            trailingContent = {
                                Icon(imageVector = shuffle, contentDescription = "Random seed color")
//                                Box(
//                                    modifier = Modifier.size(24.dp).clip(RoundedCornerShape(12.dp))
//                                        .background(color = state.seedColor)
//                                )
                            },
                            supportingContent = {
                                Box(
                                    modifier = Modifier.height(24.dp).width(48.dp).clip(RoundedCornerShape(12.dp))
                                        .background(color = state.seedColor).then(if (isInk) Modifier.inkletBorder() else Modifier)
                                )
                            }
                        )
                        SegmentedListItem(
                            content = { Text(text = stringResource(Res.string.random_color_startup)) },
                            onClick = { settingsModel.toggleRandomColorStartup(!state.randomColorStartup) },
                            trailingContent = {
                                ThemedSwitch(
                                    isInk = isInk,
                                    checked = state.randomColorStartup,
                                    onCheckedChange = { settingsModel.toggleRandomColorStartup(it) })
                            },
                            shapes = ListItemDefaults.segmentedShapes(3, 6),
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            //                enabled = isDarkMode
                        )
                        SegmentedListItem(
                            content = { Text(text = stringResource(Res.string.amoled_mode)) },
                            onClick = { settingsModel.toggleAmoled(!state.isAmoled) },
                            trailingContent = {
                                ThemedSwitch(
                                    isInk = isInk,
                                    checked = state.isAmoled,
                                    onCheckedChange = { settingsModel.toggleAmoled(it) })
                            },
                            shapes = ListItemDefaults.segmentedShapes(4, 6),
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            //                enabled = isDarkMode
                        )
                        SegmentedListItem(
                            content = { Text(text = stringResource(Res.string.variant)) },
                            supportingContent = { Text(state.paletteStyle.name) },
                            shapes = ListItemDefaults.segmentedShapes(5, if (expanded) 7 else 6),
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            onClick = { expanded = !expanded },
                            trailingContent = {
                                Icon(
                                    keyboard_arrow_down,
                                    contentDescription = if (expanded) stringResource(Res.string.collapse) else stringResource(Res.string.expand),
                                    modifier = Modifier.rotate(rotationAngle)
                                )
                            }
                        )
                        AnimatedVisibility(expanded) {
                            Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
                                repeat(availablePalettes.size) { index ->
                                    SegmentedListItem(
                                        content = { Text(availablePalettes[index].name) },
                                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                                        shapes = ListItemDefaults.segmentedShapes(
                                            index = index,
                                            count = availablePalettes.size
                                        ),
                                        onClick = { settingsModel.setPaletteStyle(availablePalettes[index]) },
                                        selected = availablePalettes[index].name == state.paletteStyle.name,
                                        leadingContent = {
                                            ThemedRadioButton(
                                                isInk = isInk,
                                                selected = availablePalettes[index].name == state.paletteStyle.name,
                                                onClick = { settingsModel.setPaletteStyle(availablePalettes[index]) })
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
                ListItemSection(stringResource(Res.string.about)) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
                    ) {
                        SegmentedListItem(
                            content = { Text(text = "${stringResource(Res.string.version)} ${BuildKonfig.version}") },
                            onClick = { uriHandler.openUri(LATEST_RELEASE_URL) },
                            trailingContent = {
                                OpenBrowserIcon()
                            },
                            shapes = ListItemDefaults.segmentedShapes(0, 4),
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        )
                        SegmentedListItem(
                            content = { Text(text = stringResource(Res.string.kanji_rec_model)) },
                            onClick = { uriHandler.openUri(MODEL_SOURCE_URL) },
                            supportingContent = {
                                Text(stringResource(Res.string.kanji_rec_model_creator))
                            },
                            trailingContent = {
                                OpenBrowserIcon()
                            },
                            shapes = ListItemDefaults.segmentedShapes(1, 4),
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        )
                        SegmentedListItem(
                            content = { Text(text = stringResource(Res.string.source_code)) },
                            onClick = { uriHandler.openUri(SOURCE_CODE_URL) },
                            trailingContent = {
                                OpenBrowserIcon()
                            },
                            shapes = ListItemDefaults.segmentedShapes(2, 4),
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        )
                        SegmentedListItem(
                            content = { Text(text = stringResource(Res.string.website)) },
                            onClick = { uriHandler.openUri(WEBSITE_URL) },
                            trailingContent = {
                                OpenBrowserIcon()
                            },
                            shapes = ListItemDefaults.segmentedShapes(3, 4),
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text("${stringResource(Res.string.owner_message)} ❤\uFE0F")
                }
            }
            ScrollBar(
                scrollState = scrollState,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            )
        }
    }
}