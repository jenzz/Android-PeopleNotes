package com.jenzz.peoplenotes.feature.home.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
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
import com.jenzz.peoplenotes.common.data.people.People
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.data.people.di.FirstName
import com.jenzz.peoplenotes.common.data.people.di.LastName
import com.jenzz.peoplenotes.common.data.time.formatFullDateTime
import com.jenzz.peoplenotes.common.ui.TextResource
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.showShortToast
import com.jenzz.peoplenotes.common.ui.theme.PeopleNotesTheme
import com.jenzz.peoplenotes.common.ui.theme.elevation
import com.jenzz.peoplenotes.common.ui.theme.spacing
import com.jenzz.peoplenotes.common.ui.widgets.MultiFloatingActionButtonContentOverlay
import com.jenzz.peoplenotes.common.ui.widgets.MultiFloatingActionButtonState.Collapsed
import com.jenzz.peoplenotes.common.ui.widgets.StaggeredVerticalGrid
import com.jenzz.peoplenotes.ext.toNonEmptyString
import com.jenzz.peoplenotes.feature.destinations.AddPersonScreenDestination
import com.jenzz.peoplenotes.feature.destinations.PersonDetailsScreenDestination
import com.jenzz.peoplenotes.feature.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.time.LocalDateTime

@Destination(start = true)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    HomeContent(
        state = viewModel.state,
        onListStyleChange = viewModel::onListStyleChange,
        onFilterChange = viewModel::onFilterChange,
        onClick = { person ->
            navigator.navigate(PersonDetailsScreenDestination(person.id))
        },
        onDeleteRequest = viewModel::onDeleteRequest,
        onDeleteConfirm = viewModel::onDeleteConfirm,
        onDeleteCancel = viewModel::onDeleteCancel,
        onDeleteWithNotes = viewModel::onDeleteWithNotes,
        onDeleteWithNotesCancel = viewModel::onDeleteWithNotesCancel,
        onSortByChange = viewModel::onSortByChange,
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
private fun HomeContent(
    state: HomeUiState,
    onListStyleChange: (ListStyle) -> Unit,
    onFilterChange: (String) -> Unit,
    onClick: (Person) -> Unit,
    onDeleteRequest: (Person) -> Unit,
    onDeleteConfirm: (Person) -> Unit,
    onDeleteCancel: () -> Unit,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancel: () -> Unit,
    onSortByChange: (SortBy) -> Unit,
    onAddPersonManuallyClick: () -> Unit,
    onImportFromContactsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onToastMessageShown: () -> Unit,
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val floatingActionButtonState = rememberSaveable { mutableStateOf(Collapsed) }
    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            HomeFloatingActionButton(
                state = floatingActionButtonState.value,
                onStateChange = { state -> floatingActionButtonState.value = state },
                onAddPersonManuallyClick = onAddPersonManuallyClick,
                onImportFromContactsClick = onImportFromContactsClick,
            )
        }
    ) {
        Column {
            HomeTopAppBar(
                modifier = Modifier.padding(
                    start = MaterialTheme.spacing.medium,
                    top = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                ),
                peopleCount = state.people.persons.size,
                filter = state.filter,
                showActions = state.showActions,
                onFilterChange = onFilterChange,
                listStyle = state.listStyle,
                onListStyleChange = onListStyleChange,
                sortBy = state.sortBy,
                onSortByChange = onSortByChange,
                onSettingsClick = onSettingsClick,
            )
            when {
                state.isLoading ->
                    HomeLoading()
                state.isEmptyFiltered ->
                    HomeEmpty(
                        text = R.string.empty_people_filtered,
                        icon = R.drawable.ic_sentiment_very_dissatisfied,
                    )
                state.isEmpty ->
                    HomeEmpty(
                        text = R.string.empty_people,
                        icon = R.drawable.ic_people,
                    )
                else ->
                    HomeLoaded(
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
        )
    }
    if (state.toastMessage != null) {
        val message = state.toastMessage.text.asString(context.resources)
        context.showShortToast(message)
        onToastMessageShown()
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
private fun HomeEmpty(
    @StringRes text: Int,
    @DrawableRes icon: Int,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            painter = painterResource(id = icon),
            contentDescription = stringResource(id = R.string.empty_people),
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
        Text(
            text = stringResource(id = text),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.caption,
        )
    }
}

@Composable
private fun HomeLoaded(
    state: HomeUiState,
    onClick: (Person) -> Unit,
    onDeleteRequest: (Person) -> Unit,
    onDeleteConfirm: (Person) -> Unit,
    onDeleteCancel: () -> Unit,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancel: () -> Unit,
) {
    when (state.listStyle) {
        ListStyle.Rows ->
            HomeLoadedRows(
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
            HomeLoadedGrid(
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
private fun HomeLoadedRows(
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
private fun HomeLoadedGrid(
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
    Image(
        painter = ColorPainter(Color.Blue), // TODO JD
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(84.dp)
            .clip(RoundedCornerShape(corner = CornerSize(16.dp)))
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
private fun HomeContentPreview(
    @PreviewParameter(HomePreviewParameterProvider::class)
    state: HomeUiState,
) {
    PeopleNotesTheme {
        Surface {
            HomeContent(
                state = state,
                onListStyleChange = {},
                onFilterChange = {},
                onClick = {},
                onDeleteRequest = {},
                onDeleteConfirm = {},
                onDeleteCancel = {},
                onDeleteWithNotes = {},
                onDeleteWithNotesCancel = {},
                onSortByChange = {},
                onAddPersonManuallyClick = {},
                onImportFromContactsClick = {},
                onSettingsClick = {},
                onToastMessageShown = {},
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
            people = People(
                persons = emptyList(),
                totalCount = 0
            ),
            deleteConfirmation = null,
            deleteWithNotesConfirmation = null,
            toastMessage = null,
        ),
        HomeUiState(
            isLoading = false,
            filter = "",
            listStyle = ListStyle.Rows,
            sortBy = SortBy.DEFAULT,
            people = People(
                persons = (1..10).map { i ->
                    Person(
                        id = PersonId(i),
                        firstName = FirstName("$i First Name".toNonEmptyString()),
                        lastName = LastName("Last Name".toNonEmptyString()),
                        lastModified = LocalDateTime.now(),
                    )
                },
                totalCount = 10
            ),
            deleteConfirmation = null,
            deleteWithNotesConfirmation = null,
            toastMessage = null,
        ),
        HomeUiState(
            isLoading = false,
            filter = "",
            listStyle = ListStyle.Rows,
            sortBy = SortBy.DEFAULT,
            people = People(
                persons = emptyList(),
                totalCount = 0
            ),
            deleteConfirmation = PersonId(1),
            deleteWithNotesConfirmation = null,
            toastMessage =
            ToastMessage(
                text = TextResource.fromText("User Message 1")
            ),
        ),
    ),
)
