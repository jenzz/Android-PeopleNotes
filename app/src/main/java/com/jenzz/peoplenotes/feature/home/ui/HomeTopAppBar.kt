package com.jenzz.peoplenotes.feature.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.TextResource

@Composable
fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    peopleCount: Int,
    showActions: Boolean,
    filter: String,
    onFilterChange: (String) -> Unit,
    listStyle: ListStyle,
    onListStyleChange: (ListStyle) -> Unit,
    sortBy: SortBy,
    onSortByChange: (SortBy) -> Unit,
    onSettingsClick: () -> Unit,
) {
    Row(modifier = modifier) {
        FilterTextField(
            modifier = Modifier.weight(1f),
            peopleCount = peopleCount,
            showActions = showActions,
            filter = filter,
            onFilterChang = onFilterChange,
            listStyle = listStyle,
            onListStyleChang = onListStyleChange,
            sortBy = sortBy,
            onSortByChang = onSortByChange,
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
    peopleCount: Int,
    showActions: Boolean,
    filter: String,
    onFilterChang: (String) -> Unit,
    listStyle: ListStyle,
    onListStyleChang: (ListStyle) -> Unit,
    sortBy: SortBy,
    onSortByChang: (SortBy) -> Unit,
) {
    TextField(
        modifier = modifier
            .background(
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.05f),
                shape = RoundedCornerShape(percent = 50),
            ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        singleLine = true,
        value = filter,
        onValueChange = onFilterChang,
        placeholder = { Text(text = stringResource(R.string.search_people, peopleCount)) },
        visualTransformation =
        if (filter.isEmpty())
            VisualTransformation.None
        else
            VisualTransformation { text ->
                TransformedText(
                    text = AnnotatedString(text.text + " ($peopleCount)"),
                    offsetMapping = OffsetMapping.Identity,
                )
            },
        trailingIcon = {
            AnimatedVisibility(
                visible = showActions,
                enter = fadeIn(animationSpec = tween(700)),
                exit = fadeOut(animationSpec = tween(700)),
            ) {
                Row {
                    ListStyleAction(
                        modifier = Modifier.size(TextFieldDefaults.MinHeight),
                        listStyle = listStyle,
                        onListStyleChange = onListStyleChang
                    )
                    SortByAction(
                        modifier = Modifier.size(TextFieldDefaults.MinHeight),
                        sortBy = sortBy,
                        onSortByChang = onSortByChang
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
    modifier: Modifier = Modifier,
    listStyle: ListStyle,
    onListStyleChange: (ListStyle) -> Unit,
) {
    when (listStyle) {
        ListStyle.Rows -> {
            IconButton(
                modifier = modifier,
                onClick = { onListStyleChange(ListStyle.Grid) },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_grid),
                    contentDescription = stringResource(id = R.string.grid_view),
                )
            }
        }
        ListStyle.Grid -> {
            IconButton(
                modifier = modifier,
                onClick = { onListStyleChange(ListStyle.Rows) },
            ) {
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
    modifier: Modifier = Modifier,
    sortBy: SortBy,
    onSortByChang: (SortBy) -> Unit,
) {
    Box(modifier = modifier) {
        var expanded by rememberSaveable { mutableStateOf(false) }
        IconButton(
            modifier = Modifier.fillMaxSize(),
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
                        onSortByChang(sortByItem)
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
