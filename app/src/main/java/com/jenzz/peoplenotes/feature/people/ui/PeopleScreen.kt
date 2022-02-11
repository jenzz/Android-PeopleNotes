package com.jenzz.peoplenotes.feature.people.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.data.people.*
import com.jenzz.peoplenotes.common.ui.*
import com.jenzz.peoplenotes.common.ui.theme.PeopleNotesTheme
import com.jenzz.peoplenotes.common.ui.theme.elevation
import com.jenzz.peoplenotes.common.ui.theme.spacing
import com.jenzz.peoplenotes.common.ui.widgets.*
import com.jenzz.peoplenotes.common.ui.widgets.MultiFloatingActionButtonState.Collapsed
import com.jenzz.peoplenotes.ext.*
import com.jenzz.peoplenotes.feature.destinations.AddPersonScreenDestination
import com.jenzz.peoplenotes.feature.destinations.NotesScreenDestination
import com.jenzz.peoplenotes.feature.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.time.LocalDateTime

@Destination(start = true)
@Composable
fun PeopleScreen(
    viewModel: PeopleViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    val state by rememberFlowWithLifecycle(viewModel.state)
        .collectAsState(initial = viewModel.initialState)
    PeopleContent(
        state = state,
        onSearchBarStateChange = viewModel::onSearchBarStateChange,
        onClick = { person ->
            navigator.navigate(NotesScreenDestination(person.id))
        },
        onDeleteRequest = viewModel::onDeleteRequest,
        onDeleteConfirm = viewModel::onDeleteConfirm,
        onDeleteCancel = viewModel::onDeleteCancel,
        onDeleteWithNotes = viewModel::onDeleteWithNotes,
        onDeleteWithNotesCancel = viewModel::onDeleteWithNotesCancel,
        onAddPersonManuallyClick = {
            navigator.navigate(AddPersonScreenDestination)
        },
        onImportFromContactsClick = {  /* TODO JD */ },
        onSettingsClick = {
            navigator.navigate(SettingsScreenDestination)
        },
        onToastMessageShown = viewModel::onToastMessageShown,
    )
}

@Composable
private fun PeopleContent(
    state: PeopleUiState,
    onSearchBarStateChange: (SearchBarState) -> Unit,
    onClick: (Person) -> Unit,
    onDeleteRequest: (Person) -> Unit,
    onDeleteConfirm: (Person) -> Unit,
    onDeleteCancel: () -> Unit,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancel: () -> Unit,
    onAddPersonManuallyClick: () -> Unit,
    onImportFromContactsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onToastMessageShown: (Long) -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    var floatingActionButtonState by rememberSaveable { mutableStateOf(Collapsed) }
    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            PeopleFloatingActionButton(
                state = floatingActionButtonState,
                onStateChange = { state -> floatingActionButtonState = state },
                onAddPersonManuallyClick = onAddPersonManuallyClick,
                onImportFromContactsClick = onImportFromContactsClick,
            )
        }
    ) {
        Column {
            SearchBar(
                modifier = Modifier.padding(
                    start = MaterialTheme.spacing.medium,
                    top = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                ),
                state = state.searchBarState,
                onStateChange = onSearchBarStateChange,
                showActions = state.showActions,
                placeholder = stringResource(R.string.search_people, state.people.persons.size),
                visualTransformation = SuffixVisualTransformation(
                    text = state.searchBarState.searchTerm,
                    suffix = " (${state.people.persons.size})",
                ),
                onSettingsClick = onSettingsClick,
            )
            when {
                state.isLoading ->
                    LoadingView()
                state.isEmptyFiltered(state.searchBarState) ->
                    EmptyView(
                        modifier = Modifier.fillMaxSize(),
                        text = stringResource(id = R.string.empty_people_filtered),
                        icon = R.drawable.ic_sentiment_very_dissatisfied,
                    )
                state.isEmpty ->
                    EmptyView(
                        modifier = Modifier.fillMaxSize(),
                        text = stringResource(id = R.string.empty_people),
                        icon = R.drawable.ic_people,
                    )
                else ->
                    PeopleLoaded(
                        state = state,
                        onClick = onClick,
                        onDeleteRequest = onDeleteRequest,
                        onDeleteConfirm = onDeleteConfirm,
                        onDeleteCancel = onDeleteCancel,
                        onDeleteWithNotes = onDeleteWithNotes,
                        onDeleteWithNotesCancel = onDeleteWithNotesCancel,
                    )
            }
        }
        MultiFloatingActionButtonContentOverlay(
            modifier = Modifier.fillMaxSize(),
            state = floatingActionButtonState,
            onStateChange = { state -> floatingActionButtonState = state }
        )
    }
    state.toastMessage?.let { toastMessage ->
        val context = LocalContext.current
        val message = toastMessage.text.asString()
        context.showShortToast(message)
        onToastMessageShown(toastMessage.id)
    }
}

@Composable
private fun PeopleLoaded(
    state: PeopleUiState,
    onClick: (Person) -> Unit,
    onDeleteRequest: (Person) -> Unit,
    onDeleteConfirm: (Person) -> Unit,
    onDeleteCancel: () -> Unit,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancel: () -> Unit,
) {
    when (state.searchBarState.listStyle) {
        ListStyle.Rows ->
            PeopleLoadedRows(
                people = state.people.persons,
                deleteConfirmation = state.deleteConfirmation,
                deleteWithNotesConfirmation = state.deleteWithNotesConfirmation,
                onClick = onClick,
                onDeleteRequest = onDeleteRequest,
                onDeleteConfirm = onDeleteConfirm,
                onDeleteCancel = onDeleteCancel,
                onDeleteWithNotes = onDeleteWithNotes,
                onDeleteWithNotesCancel = onDeleteWithNotesCancel,
            )
        ListStyle.Grid ->
            PeopleLoadedGrid(
                people = state.people.persons,
                deleteConfirmation = state.deleteConfirmation,
                deleteWithNotesConfirmation = state.deleteWithNotesConfirmation,
                onClick = onClick,
                onDeleteRequest = onDeleteRequest,
                onDeleteConfirm = onDeleteConfirm,
                onDeleteCancel = onDeleteCancel,
                onDeleteWithNotes = onDeleteWithNotes,
                onDeleteWithNotesCancel = onDeleteWithNotesCancel,
            )
    }
}

@Composable
private fun PeopleLoadedRows(
    people: List<Person>,
    deleteConfirmation: PersonId?,
    deleteWithNotesConfirmation: PersonId?,
    onClick: (Person) -> Unit,
    onDeleteRequest: (Person) -> Unit,
    onDeleteConfirm: (Person) -> Unit,
    onDeleteCancel: () -> Unit,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancel: () -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(all = MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(space = MaterialTheme.spacing.medium),
    ) {
        items(people) { person ->
            PersonRow(
                person = person,
                showDeleteDialog = person.id == deleteConfirmation,
                showDeleteWithNotesDialog = person.id == deleteWithNotesConfirmation,
                onClick = onClick,
                onDeleteRequest = onDeleteRequest,
                onDeleteConfirm = onDeleteConfirm,
                onDeleteCancel = onDeleteCancel,
                onDeleteWithNotes = onDeleteWithNotes,
                onDeleteWithNotesCancel = onDeleteWithNotesCancel,
            )
        }
    }
}

@Composable
private fun PeopleLoadedGrid(
    people: List<Person>,
    deleteConfirmation: PersonId?,
    deleteWithNotesConfirmation: PersonId?,
    onClick: (Person) -> Unit,
    onDeleteRequest: (Person) -> Unit,
    onDeleteConfirm: (Person) -> Unit,
    onDeleteCancel: () -> Unit,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancel: () -> Unit,
) {
    StaggeredVerticalGrid(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(all = MaterialTheme.spacing.small),
        maxColumnWidth = 220.dp,
    ) {
        people.forEach { person ->
            PersonGrid(
                modifier = Modifier.padding(all = MaterialTheme.spacing.small),
                person = person,
                showDeleteDialog = person.id == deleteConfirmation,
                showDeleteWithNotesDialog = person.id == deleteWithNotesConfirmation,
                onClick = onClick,
                onDeleteRequest = onDeleteRequest,
                onDeleteConfirm = onDeleteConfirm,
                onDeleteCancel = onDeleteCancel,
                onDeleteWithNotes = onDeleteWithNotes,
                onDeleteWithNotesCancel = onDeleteWithNotesCancel,
            )
        }
    }
}

@Composable
private fun PersonRow(
    person: Person,
    showDeleteDialog: Boolean,
    showDeleteWithNotesDialog: Boolean,
    onClick: (Person) -> Unit,
    onDeleteRequest: (Person) -> Unit,
    onDeleteConfirm: (Person) -> Unit,
    onDeleteCancel: () -> Unit,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancel: () -> Unit,
) {
    PersonCard(
        person = person,
        showDeleteDialog = showDeleteDialog,
        showDeleteWithNotesDialog = showDeleteWithNotesDialog,
        onClick = onClick,
        onDeleteRequest = onDeleteRequest,
        onDeleteConfirm = onDeleteConfirm,
        onDeleteCancel = onDeleteCancel,
        onDeleteWithNotes = onDeleteWithNotes,
        onDeleteWithNotesCancel = onDeleteWithNotesCancel,
    ) {
        Row {
            PersonImage(
                modifier = Modifier.padding(all = MaterialTheme.spacing.medium),
                person = person
            )
            Column(
                modifier = Modifier
                    .padding(all = MaterialTheme.spacing.extraLarge)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = person.fullName, style = MaterialTheme.typography.h6)
                Text(
                    text = stringResource(
                        id = R.string.last_modified_at,
                        person.lastModified.formatFullDateTime(),
                    ),
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}

@Composable
private fun PersonGrid(
    modifier: Modifier = Modifier,
    person: Person,
    showDeleteDialog: Boolean,
    showDeleteWithNotesDialog: Boolean,
    onClick: (Person) -> Unit,
    onDeleteRequest: (Person) -> Unit,
    onDeleteConfirm: (Person) -> Unit,
    onDeleteCancel: () -> Unit,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancel: () -> Unit,
) {
    PersonCard(
        modifier = modifier,
        person = person,
        showDeleteDialog = showDeleteDialog,
        showDeleteWithNotesDialog = showDeleteWithNotesDialog,
        onClick = onClick,
        onDeleteRequest = onDeleteRequest,
        onDeleteConfirm = onDeleteConfirm,
        onDeleteCancel = onDeleteCancel,
        onDeleteWithNotes = onDeleteWithNotes,
        onDeleteWithNotesCancel = onDeleteWithNotesCancel,
    ) {
        Column {
            PersonImage(
                modifier = Modifier.padding(all = MaterialTheme.spacing.medium),
                person = person
            )
            Column(
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.spacing.extraLarge,
                        end = MaterialTheme.spacing.extraLarge,
                        bottom = MaterialTheme.spacing.extraLarge,
                    )
                    .fillMaxWidth()
            ) {
                Text(text = person.fullName, style = MaterialTheme.typography.h6)
                Text(
                    text = stringResource(
                        id = R.string.last_modified_at,
                        person.lastModified.formatFullDateTime(),
                    ),
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}

@Composable
private fun PersonCard(
    modifier: Modifier = Modifier,
    person: Person,
    showDeleteDialog: Boolean,
    showDeleteWithNotesDialog: Boolean,
    onClick: (Person) -> Unit,
    onDeleteRequest: (Person) -> Unit,
    onDeleteConfirm: (Person) -> Unit,
    onDeleteCancel: () -> Unit,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancel: () -> Unit,
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
                onClick = { onClick(person) },
                onLongClick = { selected = true },
            )
        ) {
            content()
            PersonDropDownMenu(
                person = person,
                selected = selected,
                onDismissRequest = { selected = false },
            ) { person ->
                selected = false
                onDeleteRequest(person)
            }
        }
    }
    if (showDeleteDialog) {
        DeletePersonDialog(
            person = person,
            onDeleteConfirm = onDeleteConfirm,
            onDeleteCancel = onDeleteCancel,
        )
    }
    if (showDeleteWithNotesDialog) {
        DeletePersonWithNotesDialog(
            person = person,
            onDeleteWithNotes = onDeleteWithNotes,
            onDeleteWithNotesCancel = onDeleteWithNotesCancel,
        )
    }
}

@Composable
private fun DeletePersonDialog(
    person: Person,
    onDeleteConfirm: (Person) -> Unit,
    onDeleteCancel: () -> Unit,
) {
    AlertDialog(
        text = {
            Text(
                text = stringResource(
                    id = R.string.delete_person_dialog,
                    person.fullName
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { onDeleteConfirm(person) }) {
                Text(text = stringResource(id = R.string.yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        onDismissRequest = onDeleteCancel,
    )
}

@Composable
private fun DeletePersonWithNotesDialog(
    person: Person,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancel: () -> Unit,
) {
    AlertDialog(
        text = {
            Text(
                text = stringResource(
                    id = R.string.delete_person_with_notes_dialog,
                    person.fullName
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { onDeleteWithNotes(person) }) {
                Text(text = stringResource(id = R.string.yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDeleteWithNotesCancel) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        onDismissRequest = onDeleteWithNotesCancel,
    )
}

@Composable
private fun PersonImage(
    modifier: Modifier = Modifier,
    person: Person,
) {
    Text(
        modifier = modifier
            .size(84.dp)
            .background(
                color = person.color,
                shape = RoundedCornerShape(corner = CornerSize(16.dp))
            )
            .wrapContentHeight(),
        text = person.firstNameLetter.toString(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.h3.copy(fontWeight = FontWeight.Light),
    )
}

@Composable
private fun PersonDropDownMenu(
    person: Person,
    selected: Boolean,
    onDismissRequest: () -> Unit,
    onDelete: (Person) -> Unit,
) {
    DropdownMenu(
        expanded = selected,
        onDismissRequest = onDismissRequest,
    ) {
        DropdownMenuItem(onClick = { onDelete(person) }) {
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
private fun PeopleContentPreview(
    @PreviewParameter(PeoplePreviewParameterProvider::class)
    state: PeopleUiState,
) {
    PeopleNotesTheme {
        Surface {
            PeopleContent(
                state = state,
                onSearchBarStateChange = {},
                onClick = {},
                onDeleteRequest = {},
                onDeleteConfirm = {},
                onDeleteCancel = {},
                onDeleteWithNotes = {},
                onDeleteWithNotesCancel = {},
                onAddPersonManuallyClick = {},
                onImportFromContactsClick = {},
                onSettingsClick = {},
                onToastMessageShown = {},
            )
        }
    }
}

class PeoplePreviewParameterProvider : PreviewParameterProvider<PeopleUiState> {

    override val values: Sequence<PeopleUiState> =
        sequenceOf(
            PeopleUiState(
                isLoading = true,
                people = People(
                    persons = emptyList(),
                    totalCount = 0
                ),
                deleteConfirmation = null,
                deleteWithNotesConfirmation = null,
                toastMessage = null,
            ),
            PeopleUiState(
                isLoading = false,
                people = People(
                    persons = (1..10).map { i ->
                        Person(
                            id = PersonId(i),
                            firstName = FirstName("$i First Name".toNonEmptyString()),
                            lastName = LastName("Last Name".toNonEmptyString()),
                            color = Color.random(),
                            lastModified = LocalDateTime.now(),
                        )
                    },
                    totalCount = 10,
                ),
                deleteConfirmation = null,
                deleteWithNotesConfirmation = null,
                toastMessage = null,
            ),
            PeopleUiState(
                isLoading = false,
                people = People(
                    persons = emptyList(),
                    totalCount = 0
                ),
                deleteConfirmation = PersonId(1),
                deleteWithNotesConfirmation = null,
                toastMessage = ToastMessage(
                    text = TextResource.fromText("User Message 1")
                ),
            ),
        )
}
