package com.jenzz.peoplenotes.feature.notes.ui

import android.content.res.Configuration
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
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
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.data.people.di.FirstName
import com.jenzz.peoplenotes.common.data.people.di.LastName
import com.jenzz.peoplenotes.common.ui.ListStyle
import com.jenzz.peoplenotes.common.ui.SortByState
import com.jenzz.peoplenotes.common.ui.SuffixVisualTransformation
import com.jenzz.peoplenotes.common.ui.showShortToast
import com.jenzz.peoplenotes.common.ui.theme.PeopleNotesTheme
import com.jenzz.peoplenotes.common.ui.theme.elevation
import com.jenzz.peoplenotes.common.ui.theme.spacing
import com.jenzz.peoplenotes.common.ui.widgets.*
import com.jenzz.peoplenotes.ext.stringResourceWithStyledPlaceholders
import com.jenzz.peoplenotes.ext.toNonEmptyString
import com.jenzz.peoplenotes.feature.destinations.SettingsScreenDestination
import com.jenzz.peoplenotes.feature.notes.data.Notes
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import java.time.LocalDateTime

data class NotesScreenNavArgs(
    val personId: PersonId,
)

@Destination(navArgsDelegate = NotesScreenNavArgs::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    val searchBarState = rememberSearchBarState(sortBy = NotesSortBy.toSortByState())
    LaunchedEffect(Unit) {
        viewModel.init(searchBarState)
    }
    LaunchedEffect(searchBarState.searchTerm) {
        snapshotFlow { searchBarState.searchTerm }
            .drop(1)
            .collect { searchTerm -> viewModel.onSearchTermChange(searchTerm) }
    }
    LaunchedEffect(searchBarState.sortBy) {
        snapshotFlow { searchBarState.sortBy }
            .drop(1)
            .collect { sortBy -> viewModel.onSortByChange(sortBy.selected) }
    }
    NotesContent(
        state = viewModel.state,
        searchBarState = searchBarState,
        onSettingsClick = {
            navigator.navigate(SettingsScreenDestination)
        },
        onAddNoteClick = {
            TODO("Implement add note screen.")
        },
        onToastMessageShown = viewModel::onToastMessageShown,
    )
}

@Composable
fun NotesContent(
    state: NotesUiState,
    searchBarState: SearchBarState,
    onSettingsClick: () -> Unit,
    onAddNoteClick: () -> Unit,
    onToastMessageShown: () -> Unit,
) {
    val context = LocalContext.current
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
                state = searchBarState,
                showActions = state.showActions,
                placeholder = stringResource(id = R.string.search_notes, state.notesCount),
                visualTransformation = SuffixVisualTransformation(
                    text = searchBarState.searchTerm,
                    suffix = " (${state.notesCount})",
                ),
                onSettingsClick = onSettingsClick,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNoteClick) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(id = R.string.add),
                )
            }
        }
    ) {
        when {
            state.isLoading ->
                LoadingView()
            state is NotesUiState.Loaded -> {
                when {
                    state.isEmptyFiltered(searchBarState) ->
                        EmptyView(
                            modifier = Modifier.fillMaxSize(),
                            text = stringResource(id = R.string.empty_notes_filtered),
                            icon = R.drawable.ic_sentiment_very_dissatisfied,
                        )
                    state.isEmpty ->
                        EmptyView(
                            modifier = Modifier.fillMaxSize(),
                            text = stringResourceWithStyledPlaceholders(
                                id = R.string.empty_notes,
                                spanStyle = {
                                    SpanStyle(fontWeight = FontWeight.Bold)
                                },
                                state.notes.person.fullName,
                            ),
                            icon = R.drawable.ic_note,
                        )
                    else ->
                        NotesLoaded(
                            searchBarState = searchBarState,
                            state = state,
                        )
                }
            }
        }
    }
    state.toastMessage?.let { toastMessage ->
        val message = toastMessage.text.asString(context.resources)
        context.showShortToast(message)
        onToastMessageShown()
    }
}

@Composable
private fun NotesLoaded(
    state: NotesUiState.Loaded,
    searchBarState: SearchBarState,
) {
    when (searchBarState.listStyle) {
        ListStyle.Rows ->
            NotesLoadedRows(
                notes = state.notes.notes,
            )
        ListStyle.Grid ->
            NotesLoadedGrid(
                notes = state.notes.notes,
            )
    }
}

@Composable
private fun NotesLoadedRows(
    notes: NotesList,
) {
    LazyColumn(
        contentPadding = PaddingValues(all = MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(space = MaterialTheme.spacing.medium),
    ) {
        items(notes.items) { note ->
            NoteRow(note = note)
        }
    }
}

@Composable
private fun NotesLoadedGrid(
    notes: NotesList,
) {
    StaggeredVerticalGrid(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(all = MaterialTheme.spacing.small),
        maxColumnWidth = 220.dp,
    ) {
        notes.items.forEach { note ->
            NoteGrid(
                modifier = Modifier.padding(all = MaterialTheme.spacing.small),
                note = note,
            )
        }
    }
}

@Composable
private fun NoteRow(
    note: Note,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = MaterialTheme.elevation.small,
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
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
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = MaterialTheme.elevation.small,
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
    ) {
        Text(
            modifier = Modifier.padding(all = MaterialTheme.spacing.medium),
            text = note.text.value,
        )
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
                searchBarState = SearchBarState(
                    searchTerm = "",
                    listStyle = ListStyle.DEFAULT,
                    sortBy = SortByState(items = emptyList()),
                ),
                onSettingsClick = {},
                onAddNoteClick = {},
                onToastMessageShown = {},
            )
        }
    }
}

class NotesPreviewParameterProvider : PreviewParameterProvider<NotesUiState> {

    private val loadedState: NotesUiState.Loaded
        get() {
            val person = Person(
                id = PersonId(1),
                firstName = FirstName("First Name".toNonEmptyString()),
                lastName = LastName("Last Name".toNonEmptyString()),
                lastModified = LocalDateTime.now(),
            )
            return NotesUiState.Loaded(
                isLoading = false,
                notes = Notes(
                    person = person,
                    notes = NotesList(
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
            NotesUiState.InitialLoad,
            loadedState,
        )
}
