package com.jenzz.peoplenotes.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.TextResource

@Composable
fun HomeTopAppBar(
    showActions: Boolean,
    filter: String,
    onFilterChanged: (String) -> Unit,
    listStyle: ListStyle,
    onListStyleChanged: (ListStyle) -> Unit,
    sortBy: SortBy,
    onSortByChanged: (SortBy) -> Unit,
    onSettingsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(start = 8.dp, top = 8.dp, end = 8.dp)
    ) {
        FilterTextField(
            modifier = Modifier.weight(1f),
            showActions = showActions,
            filter = filter,
            onFilterChanged = onFilterChanged,
            listStyle = listStyle,
            onListStyleChanged = onListStyleChanged,
            sortBy = sortBy,
            onSortByChanged = onSortByChanged,
        )
        SettingsIcon(
            modifier = Modifier.size(TextFieldDefaults.MinHeight),
            onSettingsClick = onSettingsClick,
        )
    }
}

@Composable
private fun FilterTextField(
    modifier: Modifier = Modifier,
    showActions: Boolean,
    filter: String,
    onFilterChanged: (String) -> Unit,
    listStyle: ListStyle,
    onListStyleChanged: (ListStyle) -> Unit,
    sortBy: SortBy,
    onSortByChanged: (SortBy) -> Unit,
) {
    TextField(
        modifier = modifier
            .background(
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.05f),
                shape = RoundedCornerShape(50),
            ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        singleLine = true,
        value = filter,
        onValueChange = onFilterChanged,
        placeholder = { Text(text = stringResource(R.string.search_people)) },
        trailingIcon = {
            if (showActions) {
                Row {
                    ListStyleAction(
                        listStyle = listStyle,
                        onListStyleChanged = onListStyleChanged
                    )
                    SortByAction(
                        sortBy = sortBy,
                        onSortByChanged = onSortByChanged
                    )
                }
            }
        }
    )
}

@Composable
private fun SettingsIcon(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = onSettingsClick
    ) {
        Icon(
            imageVector = Icons.Rounded.Settings,
            tint = TextFieldDefaults.textFieldColors()
                .trailingIconColor(enabled = true, isError = false)
                .value,
            contentDescription = stringResource(id = R.string.settings),
        )
    }
}

@Composable
private fun ListStyleAction(
    listStyle: ListStyle,
    onListStyleChanged: (ListStyle) -> Unit,
) {
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
}

@Composable
private fun SortByAction(
    sortBy: SortBy,
    onSortByChanged: (SortBy) -> Unit,
) {
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
            SortBy.values().forEach { sortByItem ->
                SortByDropdownItem(
                    text = sortByItem.label,
                    isSelected = sortBy == sortByItem,
                    onClick = {
                        expanded = false
                        onSortByChanged(sortByItem)
                    },
                )
            }
        }
    }
}

@Composable
private fun SortByDropdownItem(
    text: TextResource,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
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
            text = text.asString(context.resources),
            style = style,
        )
    }
}
