package com.jenzz.peoplenotes.feature.person_details.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
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
import com.jenzz.peoplenotes.common.data.notes.Notes
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.data.people.di.FirstName
import com.jenzz.peoplenotes.common.data.people.di.LastName
import com.jenzz.peoplenotes.common.ui.SuffixVisualTransformation
import com.jenzz.peoplenotes.common.ui.showShortToast
import com.jenzz.peoplenotes.common.ui.theme.PeopleNotesTheme
import com.jenzz.peoplenotes.common.ui.theme.elevation
import com.jenzz.peoplenotes.common.ui.theme.spacing
import com.jenzz.peoplenotes.common.ui.widgets.EmptyView
import com.jenzz.peoplenotes.common.ui.widgets.LoadingView
import com.jenzz.peoplenotes.common.ui.widgets.SearchBar
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarUiState
import com.jenzz.peoplenotes.ext.stringResourceWithStyledPlaceholders
import com.jenzz.peoplenotes.ext.toNonEmptyString
import com.jenzz.peoplenotes.feature.destinations.SettingsScreenDestination
import com.jenzz.peoplenotes.feature.home.ui.ListStyle
import com.jenzz.peoplenotes.feature.home.ui.SortBy
import com.jenzz.peoplenotes.feature.person_details.data.PersonDetails
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.time.LocalDateTime

data class PersonDetailsScreenNavArgs(
    val personId: PersonId
)

@Destination(navArgsDelegate = PersonDetailsScreenNavArgs::class)
@Composable
fun PersonDetailsScreen(
    navigator: DestinationsNavigator,
    viewModel: PersonDetailsViewModel = hiltViewModel(),
) {
    PersonDetailsContent(
        state = viewModel.state,
        onSearchTermChange = viewModel::onSearchTermChange,
        onListStyleChange = viewModel::onListStyleChange,
        onSortByChange = viewModel::onSortByChange,
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
fun PersonDetailsContent(
    state: PersonDetailsUiState,
    onSearchTermChange: (String) -> Unit,
    onListStyleChange: (ListStyle) -> Unit,
    onSortByChange: (SortBy) -> Unit,
    onSettingsClick: () -> Unit,
    onAddNoteClick: () -> Unit,
    onToastMessageShown: () -> Unit,
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            val notesCount =
                (state as? PersonDetailsUiState.Loaded)?.personDetails?.notes?.notes?.size ?: 0
            SearchBar(
                modifier = Modifier.padding(
                    start = MaterialTheme.spacing.medium,
                    top = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                ),
                state = state.searchBarState,
                showActions = state.showActions,
                placeholder = stringResource(id = R.string.search_notes, notesCount),
                visualTransformation = SuffixVisualTransformation(
                    text = state.searchBarState.searchTerm,
                    suffix = " ($notesCount)",
                ),
                onSearchTermChange = onSearchTermChange,
                onListStyleChange = onListStyleChange,
                onSortByChange = onSortByChange,
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
            state is PersonDetailsUiState.Loaded -> {
                when {
                    state.isEmptyFiltered ->
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
                                state.personDetails.person.fullName,
                            ),
                            icon = R.drawable.ic_note,
                        )
                    else ->
                        PersonDetailsNotes(
                            notes = state.personDetails.notes,
                        )
                }
            }
        }
    }
    (state as? PersonDetailsUiState.Loaded)?.toastMessage?.let { toastMessage ->
        val message = toastMessage.text.asString(context.resources)
        context.showShortToast(message)
        onToastMessageShown()
    }
}

@Composable
private fun PersonDetailsNotes(
    notes: Notes,
) {
    LazyColumn(
        contentPadding = PaddingValues(all = MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(space = MaterialTheme.spacing.medium),
    ) {
        items(notes.notes) { note ->
            NoteRow(note = note)
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

@Preview(
    name = "Light Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PersonDetailsContentPreview(
    @PreviewParameter(PersonDetailsPreviewParameterProvider::class)
    state: PersonDetailsUiState,
) {
    PeopleNotesTheme {
        Surface {
            PersonDetailsContent(
                state = state,
                onSearchTermChange = {},
                onListStyleChange = {},
                onSortByChange = {},
                onSettingsClick = {},
                onAddNoteClick = {},
                onToastMessageShown = {},
            )
        }
    }
}

class PersonDetailsPreviewParameterProvider : PreviewParameterProvider<PersonDetailsUiState> {

    private val searchBarState = SearchBarUiState(
        searchTerm = "",
        listStyle = ListStyle.DEFAULT,
        sortBy = SortBy.DEFAULT,
    )

    private val loadedState: PersonDetailsUiState.Loaded
        get() {
            val person = Person(
                id = PersonId(1),
                firstName = FirstName("First Name".toNonEmptyString()),
                lastName = LastName("Last Name".toNonEmptyString()),
                lastModified = LocalDateTime.now(),
            )
            return PersonDetailsUiState.Loaded(
                isLoading = false,
                searchBarState = searchBarState,
                personDetails = PersonDetails(
                    person = person,
                    notes = Notes(
                        notes = (0..10).map { i ->
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

    override val values: Sequence<PersonDetailsUiState> =
        sequenceOf(
            PersonDetailsUiState.InitialLoad(
                searchBarState = searchBarState,
            ),
            loadedState
        )
}