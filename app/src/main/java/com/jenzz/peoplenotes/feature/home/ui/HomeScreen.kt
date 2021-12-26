package com.jenzz.peoplenotes.feature.home.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.data.notes.Note
import com.jenzz.peoplenotes.common.data.notes.NoteId
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.data.people.di.FirstName
import com.jenzz.peoplenotes.common.data.people.di.LastName
import com.jenzz.peoplenotes.common.ui.theme.PeopleNotesTheme
import com.jenzz.peoplenotes.common.ui.widgets.StaggeredVerticalGrid
import com.jenzz.peoplenotes.ext.showLongToast
import com.jenzz.peoplenotes.ext.toNonEmptyString

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAddPersonManuallyClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val context = LocalContext.current
    HomeContent(
        state = viewModel.state,
        onListStyleChanged = viewModel::onListStyleChanged,
        onFilterChanged = viewModel::onFilterChanged,
        onClick = { /* TODO JD */ },
        onDelete = { note ->
            viewModel.onDelete(note)
            context.showLongToast(R.string.note_deleted)
        },
        onSortByChanged = { sortBy ->
            viewModel.onSortByChanged(sortBy)
            context.showLongToast(
                context.getString(R.string.sorted_by, context.getString(sortBy.label))
            )
        },
        onAddPersonManuallyClick = onAddPersonManuallyClick,
        onSettingsClick = onSettingsClick,
    )
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onListStyleChanged: (ListStyle) -> Unit,
    onFilterChanged: (String) -> Unit,
    onClick: (Note) -> Unit,
    onDelete: (Note) -> Unit,
    onSortByChanged: (SortBy) -> Unit,
    onAddPersonManuallyClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            HomeTopAppBar(
                filter = state.filter,
                showActions = state.showActions,
                onFilterChanged = onFilterChanged,
                listStyle = state.listStyle,
                onListStyleChanged = onListStyleChanged,
                sortBy = state.sortBy,
                onSortByChanged = onSortByChanged,
                onSettingsClick = onSettingsClick,
            )
        },
        floatingActionButton = {
            HomeFloatingActionButton(
                onAddPersonManuallyClick = onAddPersonManuallyClick,
                onImportFromContactsClick = {
                    context.showLongToast(R.string.import_from_contacts)
                }
            )
        }
    ) {
        when {
            state.isLoading ->
                HomeLoading()
            state.isEmpty ->
                HomeEmpty()
            else ->
                HomeLoaded(
                    state = state,
                    onClick = onClick,
                    onDelete = onDelete,
                )
        }
    }
}

@Composable
private fun HomeLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun HomeEmpty() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_note),
            contentDescription = stringResource(id = R.string.no_notes),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(id = R.string.no_notes),
            style = MaterialTheme.typography.caption,
        )
    }
}

@Composable
private fun HomeLoaded(
    state: HomeUiState,
    onClick: (Note) -> Unit,
    onDelete: (Note) -> Unit,
) {
    when (state.listStyle) {
        ListStyle.Rows ->
            HomeLoadedRows(
                notes = state.notes,
                onClick = onClick,
                onDelete = onDelete,
            )
        ListStyle.Grid ->
            HomeLoadedGrid(
                notes = state.notes,
                onClick = onClick,
                onDelete = onDelete,
            )
    }
}

@Composable
private fun HomeLoadedRows(
    notes: List<Note>,
    onClick: (Note) -> Unit,
    onDelete: (Note) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(notes) { note ->
            NoteRow(
                note = note,
                onClick = onClick,
                onDelete = onDelete,
            )
        }
    }
}

@Composable
private fun HomeLoadedGrid(
    notes: List<Note>,
    onClick: (Note) -> Unit,
    onDelete: (Note) -> Unit,
) {
    StaggeredVerticalGrid(
        modifier = Modifier.padding(4.dp),
        maxColumnWidth = 220.dp,
    ) {
        notes.forEach { note ->
            NoteRow(
                modifier = Modifier.padding(8.dp),
                note = note,
                onClick = onClick,
                onDelete = onDelete,
            )
        }
    }
}

@Composable
private fun NoteRow(
    modifier: Modifier = Modifier,
    note: Note,
    onClick: (Note) -> Unit,
    onDelete: (Note) -> Unit,
) {
    var selected by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 110.dp)
            .combinedClickable(
                onClick = { onClick(note) },
                onLongClick = { selected = true }
            ),
        border = if (selected)
            BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface
            ) else null,
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        0.5f to Color.Transparent,
                        1f to Color.Black,
                    ),
                )
                .padding(8.dp)
        ) {
            Text(text = note.text.toString())
            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Red),
                    text = note.person.firstNameLetter.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                )
                Text(
                    text = note.person.fullName,
                    color = Color.White,
                )
                Text(
                    text = stringResource(id = R.string.edited, note.lastModified),
                    color = Color.White,
                )
            }
        }
        NoteDropDownMenu(
            selected = selected,
            onDismissRequest = { selected = false },
            onDelete = { note ->
                selected = false
                onDelete(note)
            },
            note = note
        )
    }
}

@Composable
private fun NoteDropDownMenu(
    selected: Boolean,
    onDismissRequest: () -> Unit,
    onDelete: (Note) -> Unit,
    note: Note,
) {
    DropdownMenu(
        expanded = selected,
        onDismissRequest = onDismissRequest,
    ) {
        DropdownMenuItem(onClick = { onDelete(note) }) {
            Text(
                text = stringResource(id = R.string.delete),
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colors.onSurface,
                ),
            )
        }
    }
}

@Preview(
    name = "Light Mode",
    uiMode = UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Dark Mode",
    uiMode = UI_MODE_NIGHT_YES,
)
@Composable
private fun HomeContentPreview(
    @PreviewParameter(HomePreviewParameterProvider::class)
    state: HomeUiState,
) {
    PeopleNotesTheme {
        Surface {
            HomeContent(
                state = state,
                onListStyleChanged = {},
                onFilterChanged = {},
                onClick = {},
                onDelete = {},
                onSortByChanged = {},
                onAddPersonManuallyClick = {},
                onSettingsClick = {}
            )
        }
    }
}

class HomePreviewParameterProvider : CollectionPreviewParameterProvider<HomeUiState>(
    listOf(
        HomeUiState(
            isLoading = true,
            filter = "",
            listStyle = ListStyle.Rows,
            sortBy = SortBy.DEFAULT,
            notes = emptyList(),
        ),
        HomeUiState(
            isLoading = false,
            filter = "",
            listStyle = ListStyle.Rows,
            sortBy = SortBy.DEFAULT,
            notes = (0..10).map { i ->
                Note(
                    id = NoteId(i),
                    text = "Note $i".toNonEmptyString(),
                    lastModified = "2012-10-03 12:45",
                    person = Person(
                        id = PersonId(i),
                        firstName = FirstName("$i First Name".toNonEmptyString()),
                        lastName = LastName("$i Last Name".toNonEmptyString()),
                    ),
                )
            },
        ),
    )
)
