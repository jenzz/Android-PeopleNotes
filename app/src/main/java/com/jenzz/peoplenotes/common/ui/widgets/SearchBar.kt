package com.jenzz.peoplenotes.common.ui.widgets

import android.os.Parcelable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.*
import kotlinx.parcelize.Parcelize

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    state: SearchBarState,
    onStateChange: (SearchBarState) -> Unit,
    showActions: Boolean,
    placeholder: String,
    visualTransformation: () -> VisualTransformation,
    onSettingsClick: () -> Unit,
) {
    Row(modifier = modifier) {
        SearchTextField(
            modifier = Modifier.weight(1f),
            placeholder = placeholder,
            visualTransformation = visualTransformation,
            showActions = showActions,
            searchTerm = state.searchTerm,
            onSearchTermChange = { searchTerm -> onStateChange(state.copy(searchTerm = searchTerm)) },
            listStyle = state.listStyle,
            onListStyleChange = { listStyle -> onStateChange(state.copy(listStyle = listStyle)) },
            sortBy = state.sortBy,
            onSortByChange = { selectedSortBy ->
                onStateChange(
                    state.copy(
                        sortBy = state.sortBy.copy(
                            items = state.sortBy.items.map { sortBy ->
                                sortBy.copy(isSelected = sortBy == selectedSortBy)
                            }
                        )
                    )
                )
            },
        )
        SettingsIcon(
            modifier = Modifier.size(TextFieldDefaults.MinHeight),
            onSettingsClick = onSettingsClick,
        )
    }
}

@Composable
private fun SearchTextField(
    modifier: Modifier = Modifier,
    placeholder: String,
    visualTransformation: () -> VisualTransformation,
    showActions: Boolean,
    searchTerm: String,
    onSearchTermChange: (String) -> Unit,
    listStyle: ListStyle,
    onListStyleChange: (ListStyle) -> Unit,
    sortBy: SortByState,
    onSortByChange: (SortBy) -> Unit,
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
        value = searchTerm,
        onValueChange = onSearchTermChange,
        placeholder = { Text(text = placeholder) },
        visualTransformation = visualTransformation(),
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
                        onListStyleChange = onListStyleChange
                    )
                    SortByAction(
                        modifier = Modifier.size(TextFieldDefaults.MinHeight),
                        sortBy = sortBy,
                        onSortByChange = onSortByChange
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
    sortBy: SortByState,
    onSortByChange: (SortBy) -> Unit,
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
            sortBy.items.forEach { item ->
                SortByDropdownItem(
                    text = item.label,
                    isSelected = item.isSelected,
                    onClick = {
                        expanded = false
                        onSortByChange(item)
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
            text = text.asString(),
            style = style,
        )
    }
}

@Parcelize
data class SearchBarState(
    val searchTerm: String,
    val listStyle: ListStyle,
    val sortBy: SortByState,
) : Parcelable
