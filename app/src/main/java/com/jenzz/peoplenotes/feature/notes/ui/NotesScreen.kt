package com.jenzz.peoplenotes.feature.notes.ui

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.data.notes.Note
import com.jenzz.peoplenotes.common.data.notes.NoteId
import com.jenzz.peoplenotes.common.data.notes.NotesList
import com.jenzz.peoplenotes.common.data.people.FirstName
import com.jenzz.peoplenotes.common.data.people.LastName
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.ui.ListStyle
import com.jenzz.peoplenotes.common.ui.SortByState
import com.jenzz.peoplenotes.common.ui.SuffixVisualTransformation
import com.jenzz.peoplenotes.common.ui.ToastMessageId
import com.jenzz.peoplenotes.common.ui.theme.PeopleNotesTheme
import com.jenzz.peoplenotes.common.ui.theme.elevation
import com.jenzz.peoplenotes.common.ui.theme.spacing
import com.jenzz.peoplenotes.common.ui.widgets.*
import com.jenzz.peoplenotes.ext.random
import com.jenzz.peoplenotes.ext.rememberFlowWithLifecycle
import com.jenzz.peoplenotes.ext.stringResourceWithBoldPlaceholders
import com.jenzz.peoplenotes.ext.toNonEmptyString
import com.jenzz.peoplenotes.feature.destinations.AddNoteScreenDestination
import com.jenzz.peoplenotes.feature.destinations.DeleteNoteDialogDestination
import com.jenzz.peoplenotes.feature.destinations.SettingsScreenDestination
import com.jenzz.peoplenotes.feature.notes.data.Notes
import com.jenzz.peoplenotes.feature.notes.ui.dialogs.DeleteNoteDialogResult
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import java.time.LocalDateTime

data class NotesScreenNavArgs(
    val personId: PersonId,
)

@Destination(navArgsDelegate = NotesScreenNavArgs::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel = hiltViewModel(),
    navArgs: NotesScreenNavArgs,
    navigator: DestinationsNavigator,
    deleteNoteResultRecipient: ResultRecipient<DeleteNoteDialogDestination, DeleteNoteDialogResult>,
) {
    val state by rememberFlowWithLifecycle(viewModel.state)
        .collectAsState(initial = viewModel.initialState)
    HandleDeleteConfirmation(
        state = state,
        navigator = navigator,
        resultRecipient = deleteNoteResultRecipient,
        onDeleteConfirm = viewModel::onDeleteConfirm,
        onDeleteCancel = viewModel::onDeleteCancel,
    )
    NotesContent(
        state = state,
        onSearchBarStateChanged = viewModel::onSearchBarStateChange,
        onClick = { note ->
            navigator.navigate(
                AddNoteScreenDestination(personId = navArgs.personId, noteId = note.id)
            )
        },
        onDelete = viewModel::onDelete,
        onSettingsClick = {
            navigator.navigate(SettingsScreenDestination)
        },
        onAddNoteClick = {
            navigator.navigate(
                AddNoteScreenDestination(personId = navArgs.personId)
            )
        },
        onToastShown = viewModel::onToastShown,
    )
}

@Composable
private fun HandleDeleteConfirmation(
    state: NotesUiState,
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<DeleteNoteDialogDestination, DeleteNoteDialogResult>,
    onDeleteConfirm: (NoteId) -> Unit,
    onDeleteCancel: () -> Unit,
) {
    state.showDeleteConfirmation?.let { personId ->
        navigator.navigate(DeleteNoteDialogDestination(personId))
    }
    resultRecipient.onResult { result ->
        when (result) {
            is DeleteNoteDialogResult.Yes -> onDeleteConfirm(result.personId)
            is DeleteNoteDialogResult.No -> onDeleteCancel()
        }
    }
}

@Composable
fun NotesContent(
    state: NotesUiState,
    onSearchBarStateChanged: (SearchBarInput) -> Unit,
    onClick: (Note) -> Unit,
    onDelete: (NoteId) -> Unit,
    onSettingsClick: () -> Unit,
    onAddNoteClick: () -> Unit,
    onToastShown: (ToastMessageId) -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            SearchBar(
                modifier = Modifier.padding(
                    start = MaterialTheme.spacing.medium,
                    top = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                ),
                state = state.searchBar,
                onStateChange = onSearchBarStateChanged,
                showActions = state.showActions,
                placeholder = stringResource(id = R.string.search_notes, state.notesCount),
                visualTransformation = SuffixVisualTransformation(
                    text = state.searchBar.searchTerm,
                    suffix = " (${state.notesCount})",
                ),
                onSettingsClick = onSettingsClick,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNoteClick) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(id = R.string.save),
                )
            }
        }
    ) {
        when {
            state.isLoading ->
                LoadingView()
            state.isEmptyFiltered(state.searchBar) ->
                EmptyFilteredView(state.searchBar.searchTerm)
            state.isEmpty ->
                EmptyView(state.notes.requirePerson())
            else ->
                NotesLoaded(
                    state = state,
                    searchBar = state.searchBar,
                    onClick = onClick,
                    onDelete = onDelete,
                )
        }
    }
    state.toastMessage?.let { message ->
        Toast(
            message = message,
            onToastShown = onToastShown,
        )
    }
}

@Composable
private fun EmptyFilteredView(
    searchTerm: String,
) {
    EmptyView(
        modifier = Modifier.fillMaxSize(),
        text = stringResourceWithBoldPlaceholders(
            id = R.string.empty_notes_filtered,
            searchTerm,
        ),
        icon = R.drawable.ic_sentiment_very_dissatisfied,
    )
}

@Composable
private fun EmptyView(
    person: Person,
) {
    EmptyView(
        modifier = Modifier.fillMaxSize(),
        text = stringResourceWithBoldPlaceholders(
            id = R.string.empty_notes,
            person.fullName,
        ),
        icon = R.drawable.ic_note,
    )
}

@Composable
private fun NotesLoaded(
    state: NotesUiState,
    searchBar: SearchBarInput,
    onClick: (Note) -> Unit,
    onDelete: (NoteId) -> Unit,
) {
    when (searchBar.listStyle) {
        ListStyle.Rows ->
            NotesLoadedRows(
                notes = state.notes,
                onClick = onClick,
                onDelete = onDelete,
            )
        ListStyle.Grid ->
            NotesLoadedGrid(
                notes = state.notes,
                onClick = onClick,
                onDelete = onDelete,
            )
    }
}

@Composable
private fun NotesLoadedRows(
    notes: Notes,
    onClick: (Note) -> Unit,
    onDelete: (NoteId) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(all = MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(space = MaterialTheme.spacing.medium),
    ) {
        items(notes.list.items) { note ->
            NoteRow(
                note = note,
                onClick = onClick,
                onDelete = onDelete,
            )
        }
    }
}

@Composable
private fun NotesLoadedGrid(
    notes: Notes,
    onClick: (Note) -> Unit,
    onDelete: (NoteId) -> Unit,
) {
    StaggeredVerticalGrid(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(all = MaterialTheme.spacing.small),
        maxColumnWidth = 220.dp,
    ) {
        notes.list.items.forEach { note ->
            NoteGrid(
                modifier = Modifier.padding(all = MaterialTheme.spacing.small),
                note = note,
                onClick = onClick,
                onDelete = onDelete,
            )
        }
    }
}

@Composable
private fun NoteRow(
    note: Note,
    onClick: (Note) -> Unit,
    onDelete: (NoteId) -> Unit,
) {
    NoteCard(
        modifier = Modifier.fillMaxWidth(),
        note = note,
        onClick = onClick,
        onDelete = onDelete,
    ) {
        Text(
            modifier = Modifier.padding(all = MaterialTheme.spacing.medium),
            text = note.text.value,
        )
    }
}

@Composable
fun NoteGrid(
    modifier: Modifier = Modifier,
    note: Note,
    onClick: (Note) -> Unit,
    onDelete: (NoteId) -> Unit,
) {
    NoteCard(
        modifier = modifier.fillMaxWidth(),
        note = note,
        onClick = onClick,
        onDelete = onDelete,
    ) {
        Text(
            modifier = Modifier.padding(all = MaterialTheme.spacing.medium),
            text = note.text.value,
        )
    }
}

@Composable
private fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    onClick: (Note) -> Unit,
    onDelete: (NoteId) -> Unit,
    content: @Composable () -> Unit,
) {
    var selected by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = MaterialTheme.elevation.small,
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        border = if (selected)
            BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface
            ) else null,
    ) {
        Box(
            modifier = Modifier.combinedClickable(
                onClick = { onClick(note) },
                onLongClick = { selected = true },
            )
        ) {
            content()
            NoteDropDownMenu(
                note = note,
                expanded = selected,
                onDismissRequest = { selected = false },
                onDelete = { noteId ->
                    selected = false
                    onDelete(noteId)
                },
            )
        }
    }
}

@Composable
private fun NoteDropDownMenu(
    note: Note,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onDelete: (NoteId) -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        DropdownMenuItem(onClick = { onDelete(note.id) }) {
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
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun NotesContentPreview(
    @PreviewParameter(NotesPreviewParameterProvider::class)
    state: NotesUiState,
) {
    PeopleNotesTheme {
        Surface {
            NotesContent(
                state = state,
                onSearchBarStateChanged = {},
                onClick = {},
                onDelete = {},
                onSettingsClick = {},
                onAddNoteClick = {},
                onToastShown = {},
            )
        }
    }
}

class NotesPreviewParameterProvider : PreviewParameterProvider<NotesUiState> {

    private val loadedState: NotesUiState
        get() {
            val person = Person(
                id = PersonId(1),
                firstName = FirstName("First Name".toNonEmptyString()),
                lastName = LastName("Last Name".toNonEmptyString()),
                color = Color.random(),
                lastModified = LocalDateTime.now(),
            )
            return NotesUiState(
                searchBar = SearchBarInput(
                    searchTerm = "",
                    listStyle = ListStyle.DEFAULT,
                    sortBy = SortByState(items = emptyList()),
                ),
                isLoading = false,
                notes = Notes(
                    person = person,
                    list = NotesList(
                        items = (0..10).map { i ->
                            Note(
                                id = NoteId(i),
                                text = "".toNonEmptyString(),
                                lastModified = LocalDateTime.now(),
                                person = person
                            )
                        },
                        totalCount = 10,
                    ),
                ),
                toastMessage = null,
            )
        }

    override val values: Sequence<NotesUiState> =
        sequenceOf(
            loadedState,
        )
}
