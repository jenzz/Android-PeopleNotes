package com.jenzz.peoplenotes.feature.home.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.jenzz.peoplenotes.R

@Composable
fun HomeTopAppBar(
    listStyle: ListStyle?,
    onListStyleChanged: (ListStyle) -> Unit,
    sortedBy: SortBy?,
    onSortBy: (SortBy) -> Unit,
    onSettingsClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
        actions = {
            Actions(
                listStyle = listStyle,
                onListStyleChanged = onListStyleChanged,
                sortedBy = sortedBy,
                onSortBy = onSortBy,
                onSettingsClick = onSettingsClick,
            )
        }
    )
}

@Composable
private fun Actions(
    listStyle: ListStyle?,
    onListStyleChanged: (ListStyle) -> Unit,
    sortedBy: SortBy?,
    onSortBy: (SortBy) -> Unit,
    onSettingsClick: () -> Unit,
) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
        when (listStyle) {
            ListStyle.Rows -> {
                IconButton(onClick = { onListStyleChanged(ListStyle.Grid) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_grid),
                        contentDescription = stringResource(id = R.string.grid_view),
                    )
                }
            }
            ListStyle.Grid -> {
                IconButton(onClick = { onListStyleChanged(ListStyle.Rows) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_rows),
                        contentDescription = stringResource(id = R.string.rows),
                    )
                }
            }
        }
        if (sortedBy != null) {
            Box {
                var expanded by rememberSaveable { mutableStateOf(false) }
                IconButton(
                    onClick = { expanded = !expanded },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sort),
                        contentDescription = stringResource(id = R.string.sort_by),
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    SortBy.values().forEach { sortBy ->
                        SortByDropdownItem(
                            text = sortBy.label,
                            isSelected = sortBy == sortedBy,
                            onClick = {
                                expanded = false
                                onSortBy(sortBy)
                            },
                        )
                    }
                }
            }
        }
        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = stringResource(id = R.string.settings),
            )
        }
    }
}

@Composable
private fun SortByDropdownItem(
    @StringRes text: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val style = if (isSelected) {
        MaterialTheme.typography.body1.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.secondary,
        )
    } else {
        MaterialTheme.typography.body1.copy(
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colors.onSurface,
        )
    }
    DropdownMenuItem(onClick = onClick) {
        Text(
            text = stringResource(id = text),
            style = style,
        )
    }
}
