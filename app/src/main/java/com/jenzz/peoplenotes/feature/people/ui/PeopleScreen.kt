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
import com.jenzz.peoplenotes.feature.destinations.*
import com.jenzz.peoplenotes.feature.people.ui.dialogs.DeletePersonDialogResult
import com.jenzz.peoplenotes.feature.people.ui.dialogs.DeletePersonWithNotesDialogResult
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import java.time.LocalDateTime

@Destination(start = true)
@Composable
fun PeopleScreen(
    viewModel: PeopleViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    deletePersonResultRecipient: ResultRecipient<DeletePersonDialogDestination, DeletePersonDialogResult>,
    deletePersonWithNotesResultRecipient: ResultRecipient<DeletePersonWithNotesDialogDestination, DeletePersonWithNotesDialogResult>,
) {
    val state by rememberFlowWithLifecycle(viewModel.state)
        .collectAsState(initial = viewModel.initialState)
    HandleDeleteConfirmation(
        state = state,
        navigator = navigator,
        resultRecipient = deletePersonResultRecipient,
        onDeleteConfirm = viewModel::onDeleteConfirm,
        onDeleteCancel = viewModel::onDeleteCancel,
    )
    HandleDeleteWithNotesConfirmation(
        state = state,
        navigator = navigator,
        resultRecipient = deletePersonWithNotesResultRecipient,
        onDeleteWithNotesConfirm = viewModel::onDeleteWithNotesConfirm,
        onDeleteWithNotesCancel = viewModel::onDeleteWithNotesCancel,
    )
    PeopleContent(
        state = state,
        onSearchBarStateChange = viewModel::onSearchBarStateChange,
        onClick = { person ->
            navigator.navigate(NotesScreenDestination(person.id))
        },
        onDelete = viewModel::onDelete,
        onAddPersonManuallyClick = {
            navigator.navigate(AddPersonScreenDestination)
        },
        onImportFromContactsClick = {  /* TODO JD */ },
        onSettingsClick = {
            navigator.navigate(SettingsScreenDestination)
        },
        onToastShown = viewModel::onToastShown,
    )
}

@Composable
private fun HandleDeleteConfirmation(
    state: PeopleUiState,
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<DeletePersonDialogDestination, DeletePersonDialogResult>,
    onDeleteConfirm: (PersonId) -> Unit,
    onDeleteCancel: () -> Unit,
) {
    state.showDeleteConfirmation?.let { person ->
        navigator.navigate(DeletePersonDialogDestination(person))
    }
    resultRecipient.onResult { result ->
        when (result) {
            is DeletePersonDialogResult.Yes -> onDeleteConfirm(result.personId)
            is DeletePersonDialogResult.No -> onDeleteCancel()
        }
    }
}

@Composable
fun HandleDeleteWithNotesConfirmation(
    state: PeopleUiState,
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<DeletePersonWithNotesDialogDestination, DeletePersonWithNotesDialogResult>,
    onDeleteWithNotesConfirm: (PersonId) -> Unit,
    onDeleteWithNotesCancel: () -> Unit,
) {
    state.showDeleteWithNotesConfirmation?.let { personId ->
        navigator.navigate(DeletePersonWithNotesDialogDestination(personId))
    }
    resultRecipient.onResult { result ->
        when (result) {
            is DeletePersonWithNotesDialogResult.Yes -> onDeleteWithNotesConfirm(result.personId)
            is DeletePersonWithNotesDialogResult.No -> onDeleteWithNotesCancel()
        }
    }
}

@Composable
private fun PeopleContent(
    state: PeopleUiState,
    onSearchBarStateChange: (SearchBarInput) -> Unit,
    onClick: (Person) -> Unit,
    onDelete: (PersonSimplified) -> Unit,
    onAddPersonManuallyClick: () -> Unit,
    onImportFromContactsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onToastShown: (ToastMessageId) -> Unit,
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
                state = state.searchBar,
                onStateChange = onSearchBarStateChange,
                showActions = state.showActions,
                placeholder = stringResource(
                    id = R.string.search_people,
                    state.people.persons.size
                ),
                visualTransformation = SuffixVisualTransformation(
                    text = state.searchBar.searchTerm,
                    suffix = " (${state.people.persons.size})",
                ),
                onSettingsClick = onSettingsClick,
            )
            when {
                state.isLoading ->
                    LoadingView()
                state.isEmptyFiltered(state.searchBar) ->
                    EmptyFilteredView(state.searchBar.searchTerm)
                state.isEmpty ->
                    EmptyView()
                else ->
                    PeopleLoaded(
                        state = state,
                        onClick = onClick,
                        onDelete = onDelete,
                    )
            }
        }
        MultiFloatingActionButtonContentOverlay(
            modifier = Modifier.fillMaxSize(),
            state = floatingActionButtonState,
            onStateChange = { state -> floatingActionButtonState = state }
        )
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
            id = R.string.empty_people_filtered,
            searchTerm,
        ),
        icon = R.drawable.ic_sentiment_very_dissatisfied,
    )
}

@Composable
private fun EmptyView() {
    EmptyView(
        modifier = Modifier.fillMaxSize(),
        text = stringResource(id = R.string.empty_people),
        icon = R.drawable.ic_people,
    )
}

@Composable
private fun PeopleLoaded(
    state: PeopleUiState,
    onClick: (Person) -> Unit,
    onDelete: (PersonSimplified) -> Unit,
) {
    when (state.searchBar.listStyle) {
        ListStyle.Rows ->
            PeopleLoadedRows(
                people = state.people.persons,
                onClick = onClick,
                onDelete = onDelete,
            )
        ListStyle.Grid ->
            PeopleLoadedGrid(
                people = state.people.persons,
                onClick = onClick,
                onDelete = onDelete,
            )
    }
}

@Composable
private fun PeopleLoadedRows(
    people: List<Person>,
    onClick: (Person) -> Unit,
    onDelete: (PersonSimplified) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(all = MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(space = MaterialTheme.spacing.medium),
    ) {
        items(people) { person ->
            PersonRow(
                person = person,
                onClick = onClick,
                onDelete = onDelete,
            )
        }
    }
}

@Composable
private fun PeopleLoadedGrid(
    people: List<Person>,
    onClick: (Person) -> Unit,
    onDelete: (PersonSimplified) -> Unit,
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
                onClick = onClick,
                onDelete = onDelete,
            )
        }
    }
}

@Composable
private fun PersonRow(
    person: Person,
    onClick: (Person) -> Unit,
    onDelete: (PersonSimplified) -> Unit,
) {
    PersonCard(
        person = person,
        onClick = onClick,
        onDelete = onDelete,
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
    onClick: (Person) -> Unit,
    onDelete: (PersonSimplified) -> Unit,
) {
    PersonCard(
        modifier = modifier,
        person = person,
        onClick = onClick,
        onDelete = onDelete,
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
    onClick: (Person) -> Unit,
    onDelete: (PersonSimplified) -> Unit,
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
                person = person.simplified(),
                expanded = selected,
                onDismissRequest = { selected = false },
                onDelete = { person ->
                    selected = false
                    onDelete(person)
                },
            )
        }
    }
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
        text = person.firstNameLetter,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.h3.copy(fontWeight = FontWeight.Light),
    )
}

@Composable
private fun PersonDropDownMenu(
    person: PersonSimplified,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onDelete: (PersonSimplified) -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
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
                onDelete = {},
                onAddPersonManuallyClick = {},
                onImportFromContactsClick = {},
                onSettingsClick = {},
                onToastShown = {},
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
                showDeleteConfirmation = null,
                showDeleteWithNotesConfirmation = null,
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
                showDeleteConfirmation = null,
                showDeleteWithNotesConfirmation = null,
                toastMessage = null,
            ),
            PeopleUiState(
                isLoading = false,
                people = People(
                    persons = emptyList(),
                    totalCount = 0
                ),
                showDeleteConfirmation = PersonSimplified(
                    id = PersonId(1),
                    firstName = FirstName("First Name".toNonEmptyString()),
                    lastName = LastName("Last Name".toNonEmptyString()),
                ),
                showDeleteWithNotesConfirmation = null,
                toastMessage = ToastMessage(
                    text = TextResource.fromText("User Message 1")
                ),
            ),
        )
}
