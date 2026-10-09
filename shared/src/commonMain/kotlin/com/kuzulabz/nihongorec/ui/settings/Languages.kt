package com.kuzulabz.nihongorec.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.kuzulabz.nihongorec.icons.open_in_new
import com.kuzulabz.nihongorec.locale.AppLocale
import nihongorec.shared.generated.resources.Res
import nihongorec.shared.generated.resources.language
import nihongorec.shared.generated.resources.open_lang_settings
import org.jetbrains.compose.resources.stringResource

@Composable
fun LocaleListItem(currentLocale: AppLocale, isSupported: Boolean, onLocaleSelected: (locale: AppLocale?) -> Unit,) {
    var expanded by remember { mutableStateOf(false) }
    val size = AppLocale.entries.size

    Box() {
        SegmentedListItem(
            content = { Text(text = stringResource(Res.string.language)) },
            onClick = {
                if (isSupported) {
                    expanded = true
                } else {
                    onLocaleSelected(null)
                }
            },
            supportingContent = {
                Text(currentLocale.displayName)
            },
            trailingContent = {
                if (!isSupported) {
                    Icon(open_in_new, contentDescription = stringResource(Res.string.open_lang_settings))
                }
            },
            shapes = ListItemDefaults.segmentedShapes(1, 2),
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MenuDefaults.groupStandardContainerColor,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            AppLocale.entries.forEachIndexed { index, locale ->
                SelectableDropdownMenuItem(
                    text = {
                        Text(
                            text = locale.displayName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    colors = MenuDefaults.selectableItemColors(),
                    shapes = MenuDefaults.itemShape(index, size),
                    selected = locale == currentLocale,
                    onClick = {
                        onLocaleSelected(locale)
                        expanded = false
                    },
                    contentPadding = MenuDefaults.DropdownMenuItemContentPadding,
                )
            }
        }
    }
}