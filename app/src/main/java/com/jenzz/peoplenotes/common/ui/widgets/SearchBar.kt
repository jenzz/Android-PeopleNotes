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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.ListStyle
import com.jenzz.peoplenotes.common.ui.TextResource
import com.jenzz.peoplenotes.feature.people.ui.PeopleSortBy
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    state: SearchBarUiState,
    showActions: Boolean,
    placeholder: String,
    visualTransformation: () -> VisualTransformation,
    onSearchTermChange: (String) -> Unit,
    onListStyleChange: (ListStyle) -> Unit,
    onSortByChange: (PeopleSortBy) -> Unit,
    onSettingsClick: () -> Unit,
) {
    Row(modifier = modifier) {
        SearchTextField(
            modifier = Modifier.weight(1f),
            placeholder = placeholder,
            visualTransformation = visualTransformation,
            showActions = showActions,
            searchTerm = state.searchTerm,
            onSearchTermChange = onSearchTermChange,
            listStyle = state.listStyle,
            onListStyleChange = onListStyleChange,
            sortBy = state.sortBy,
            onSortByChange = onSortByChange,
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
    sortBy: PeopleSortBy,
    onSortByChange: (PeopleSortBy) -> Unit,
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
                        onSortByChang = onSortByChange
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
    sortBy: PeopleSortBy,
    onSortByChang: (PeopleSortBy) -> Unit,
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
            PeopleSortBy.values().forEach { sortByItem ->
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

@Parcelize
data class SearchBarUiState(
    val searchTerm: String,
    val listStyle: ListStyle,
    val sortBy: PeopleSortBy,
) : Parcelable {

    companion object {

        val DEFAULT = SearchBarUiState(
            searchTerm = "",
            listStyle = ListStyle.DEFAULT,
            sortBy = PeopleSortBy.DEFAULT,
        )
    }
}

class SearchBarState @Inject constructor() {

    var state by mutableStateOf(SearchBarUiState.DEFAULT)
        private set

    fun onSearchTermChange(searchTerm: String): SearchBarUiState {
        state = state.copy(searchTerm = searchTerm)
        return state
    }

    fun onListStyleChange(listStyle: ListStyle): SearchBarUiState {
        state = state.copy(listStyle = listStyle)
        return state
    }

    fun onSortByChange(sortBy: PeopleSortBy): SearchBarUiState {
        state = state.copy(sortBy = sortBy)
        return state
    }
}