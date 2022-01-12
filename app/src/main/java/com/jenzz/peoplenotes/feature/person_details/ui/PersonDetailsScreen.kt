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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import com.jenzz.peoplenotes.common.ui.theme.elevation
import com.jenzz.peoplenotes.common.ui.theme.spacing
import com.jenzz.peoplenotes.ext.toNonEmptyString
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
        onAddNoteClick = {
            TODO("Implement add note screen.")
        }
    )
}

@Composable
fun PersonDetailsContent(
    state: PersonDetailsUiState,
    onAddNoteClick: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNoteClick) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(id = R.string.add),
                )
            }
        }
    ) {
        when (state) {
            is PersonDetailsUiState.Loading ->
                PersonDetailsLoading()
            is PersonDetailsUiState.Loaded ->
                PersonDetailsLoaded(
                    personDetails = state.personDetails,
                )
        }
    }
}

@Composable
private fun PersonDetailsLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun PersonDetailsLoaded(
    personDetails: PersonDetails,
) {
    LazyColumn(
        contentPadding = PaddingValues(all = MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(space = MaterialTheme.spacing.medium),
    ) {
        items(personDetails.notes) { note ->
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
                onAddNoteClick = {},
            )
        }
    }
}

class PersonDetailsPreviewParameterProvider :
    CollectionPreviewParameterProvider<PersonDetailsUiState>(
        listOf(
            PersonDetailsUiState.Loading,
            PersonDetailsUiState.Loaded(
                personDetails = PersonDetails(
                    notes = (0..10).map { i ->
                        Note(
                            id = NoteId(i),
                            text = "".toNonEmptyString(),
                            lastModified = LocalDateTime.now(),
                            person = Person(
                                id = PersonId(i),
                                firstName = FirstName("First Name".toNonEmptyString()),
                                lastName = LastName("Last Name".toNonEmptyString()),
                                lastModified = LocalDateTime.now(),
                            )
                        )
                    }
                )
            ),
        )
    )