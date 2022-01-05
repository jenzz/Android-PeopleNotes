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
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.data.people.di.FirstName
import com.jenzz.peoplenotes.common.data.people.di.LastName
import com.jenzz.peoplenotes.common.ui.TextResource
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.showShortToast
import com.jenzz.peoplenotes.common.ui.theme.PeopleNotesTheme
import com.jenzz.peoplenotes.common.ui.widgets.StaggeredVerticalGrid
import com.jenzz.peoplenotes.ext.toNonEmptyString

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAddPersonManuallyClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    HomeContent(
        state = viewModel.state,
        onListStyleChanged = viewModel::onListStyleChanged,
        onFilterChanged = viewModel::onFilterChanged,
        onClick = { /* TODO JD */ },
        onDeleteRequested = viewModel::onDeleteRequested,
        onDeleteConfirmed = viewModel::onDeleteConfirmed,
        onDeleteCancelled = viewModel::onDeleteCancelled,
        onDeleteWithNotes = viewModel::onDeleteWithNotes,
        onDeleteWithNotesCancelled = viewModel::onDeleteWithNotesCancelled,
        onSortByChanged = viewModel::onSortByChanged,
        onAddPersonManuallyClick = onAddPersonManuallyClick,
        onImportFromContactsClick = {  /* TODO JD */ },
        onSettingsClick = onSettingsClick,
        onToastMessageShown = viewModel::onToastMessageShown,
    )
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onListStyleChanged: (ListStyle) -> Unit,
    onFilterChanged: (String) -> Unit,
    onClick: (Person) -> Unit,
    onDeleteRequested: (Person) -> Unit,
    onDeleteConfirmed: (Person) -> Unit,
    onDeleteCancelled: () -> Unit,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancelled: () -> Unit,
    onSortByChanged: (SortBy) -> Unit,
    onAddPersonManuallyClick: () -> Unit,
    onImportFromContactsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onToastMessageShown: () -> Unit,
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
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
                onImportFromContactsClick = onImportFromContactsClick,
            )
        }
    ) {
        when {
            state.isLoading ->
                HomeLoading()
            state.isEmpty ->
                HomeEmpty(
                    text = R.string.empty_people,
                    icon = R.drawable.ic_people,
                )
            state.isEmptyFiltered ->
                HomeEmpty(
                    text = R.string.empty_people_filtered,
                    icon = R.drawable.ic_sentiment_very_dissatisfied,
                )
            else ->
                HomeLoaded(
                    state = state,
                    onClick = onClick,
                    onDeleteRequested = onDeleteRequested,
                    onDeleteConfirmed = onDeleteConfirmed,
                    onDeleteCancelled = onDeleteCancelled,
                    onDeleteWithNotes = onDeleteWithNotes,
                    onDeleteWithNotesCancelled = onDeleteWithNotesCancelled,
                )
        }
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
        Spacer(modifier = Modifier.height(4.dp))
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
    onDeleteRequested: (Person) -> Unit,
    onDeleteConfirmed: (Person) -> Unit,
    onDeleteCancelled: () -> Unit,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancelled: () -> Unit,
) {
    when (state.listStyle) {
        ListStyle.Rows ->
            HomeLoadedRows(
                people = state.people,
                deleteConfirmation = state.deleteConfirmation,
                deleteWithNotesConfirmation = state.deleteWithNotesConfirmation,
                onClick = onClick,
                onDeleteRequested = onDeleteRequested,
                onDeleteConfirmed = onDeleteConfirmed,
                onDeleteCancelled = onDeleteCancelled,
                onDeleteWithNotes = onDeleteWithNotes,
                onDeleteWithNotesCancelled = onDeleteWithNotesCancelled,
            )
        ListStyle.Grid ->
            HomeLoadedGrid(
                people = state.people,
                deleteConfirmation = state.deleteConfirmation,
                deleteWithNotesConfirmation = state.deleteWithNotesConfirmation,
                onClick = onClick,
                onDeleteRequested = onDeleteRequested,
                onDeleteConfirmed = onDeleteConfirmed,
                onDeleteCancelled = onDeleteCancelled,
                onDeleteWithNotes = onDeleteWithNotes,
                onDeleteWithNotesCancelled = onDeleteWithNotesCancelled,
            )
    }
}

@Composable
private fun HomeLoadedRows(
    people: List<Person>,
    deleteConfirmation: PersonId?,
    deleteWithNotesConfirmation: PersonId?,
    onClick: (Person) -> Unit,
    onDeleteRequested: (Person) -> Unit,
    onDeleteConfirmed: (Person) -> Unit,
    onDeleteCancelled: () -> Unit,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancelled: () -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(people) { person ->
            PersonRow(
                modifier = Modifier.padding(4.dp),
                person = person,
                showDeleteDialog = person.id == deleteConfirmation,
                showDeleteWithNotesDialog = person.id == deleteWithNotesConfirmation,
                onClick = onClick,
                onDeleteRequested = onDeleteRequested,
                onDeleteConfirmed = onDeleteConfirmed,
                onDeleteCancelled = onDeleteCancelled,
                onDeleteWithNotes = onDeleteWithNotes,
                onDeleteWithNotesCancelled = onDeleteWithNotesCancelled,
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
    onDeleteRequested: (Person) -> Unit,
    onDeleteConfirmed: (Person) -> Unit,
    onDeleteCancelled: () -> Unit,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancelled: () -> Unit,
) {
    StaggeredVerticalGrid(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(4.dp),
        maxColumnWidth = 220.dp,
    ) {
        people.forEach { person ->
            PersonGrid(
                modifier = Modifier.padding(8.dp),
                person = person,
                showDeleteDialog = person.id == deleteConfirmation,
                showDeleteWithNotesDialog = person.id == deleteWithNotesConfirmation,
                onClick = onClick,
                onDeleteRequested = onDeleteRequested,
                onDeleteConfirmed = onDeleteConfirmed,
                onDeleteCancelled = onDeleteCancelled,
                onDeleteWithNotes = onDeleteWithNotes,
                onDeleteWithNotesCancelled = onDeleteWithNotesCancelled,
            )
        }
    }
}

@Composable
private fun PersonRow(
    modifier: Modifier = Modifier,
    person: Person,
    showDeleteDialog: Boolean,
    showDeleteWithNotesDialog: Boolean,
    onClick: (Person) -> Unit,
    onDeleteRequested: (Person) -> Unit,
    onDeleteConfirmed: (Person) -> Unit,
    onDeleteCancelled: () -> Unit,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancelled: () -> Unit,
) {
    PersonCard(
        modifier = modifier,
        person = person,
        showDeleteDialog = showDeleteDialog,
        showDeleteWithNotesDialog = showDeleteWithNotesDialog,
        onClick = onClick,
        onDeleteRequested = onDeleteRequested,
        onDeleteConfirmed = onDeleteConfirmed,
        onDeleteCancelled = onDeleteCancelled,
        onDeleteWithNotes = onDeleteWithNotes,
        onDeleteWithNotesCancelled = onDeleteWithNotesCancelled,
    ) {
        Row {
            PersonImage(
                modifier = Modifier.padding(8.dp),
                person = person
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = person.fullName, style = MaterialTheme.typography.h6)
                Text(text = person.lastModified, style = MaterialTheme.typography.caption)
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
    onDeleteRequested: (Person) -> Unit,
    onDeleteConfirmed: (Person) -> Unit,
    onDeleteCancelled: () -> Unit,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancelled: () -> Unit,
) {
    PersonCard(
        modifier = modifier,
        person = person,
        showDeleteDialog = showDeleteDialog,
        showDeleteWithNotesDialog = showDeleteWithNotesDialog,
        onClick = onClick,
        onDeleteRequested = onDeleteRequested,
        onDeleteConfirmed = onDeleteConfirmed,
        onDeleteCancelled = onDeleteCancelled,
        onDeleteWithNotes = onDeleteWithNotes,
        onDeleteWithNotesCancelled = onDeleteWithNotesCancelled,
    ) {
        Column {
            PersonImage(
                modifier = Modifier.padding(8.dp),
                person = person
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = person.fullName, style = MaterialTheme.typography.h6)
                Text(text = person.lastModified, style = MaterialTheme.typography.caption)
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
    onDeleteRequested: (Person) -> Unit,
    onDeleteConfirmed: (Person) -> Unit,
    onDeleteCancelled: () -> Unit,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancelled: () -> Unit,
    content: @Composable () -> Unit,
) {
    var selected by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 2.dp,
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
                onDeleteRequested(person)
            }
        }
    }
    if (showDeleteDialog) {
        DeletePersonDialog(
            person = person,
            onDeleteConfirmed = onDeleteConfirmed,
            onDeleteCancelled = onDeleteCancelled,
        )
    }
    if (showDeleteWithNotesDialog) {
        DeletePersonWithNotesDialog(
            person = person,
            onDeleteWithNotes = onDeleteWithNotes,
            onDeleteWithNotesCancelled = onDeleteWithNotesCancelled,
        )
    }
}

@Composable
private fun DeletePersonDialog(
    person: Person,
    onDeleteConfirmed: (Person) -> Unit,
    onDeleteCancelled: () -> Unit,
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
            TextButton(onClick = { onDeleteConfirmed(person) }) {
                Text(text = stringResource(id = R.string.yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDeleteCancelled) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        onDismissRequest = onDeleteCancelled,
    )
}

@Composable
private fun DeletePersonWithNotesDialog(
    person: Person,
    onDeleteWithNotes: (Person) -> Unit,
    onDeleteWithNotesCancelled: () -> Unit,
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
            TextButton(onClick = onDeleteWithNotesCancelled) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        onDismissRequest = onDeleteWithNotesCancelled,
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
                onListStyleChanged = {},
                onFilterChanged = {},
                onClick = {},
                onDeleteRequested = {},
                onDeleteConfirmed = {},
                onDeleteCancelled = {},
                onDeleteWithNotes = {},
                onDeleteWithNotesCancelled = {},
                onSortByChanged = {},
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
            people = emptyList(),
            deleteConfirmation = null,
            deleteWithNotesConfirmation = null,
            toastMessage = null,
        ),
        HomeUiState(
            isLoading = false,
            filter = "",
            listStyle = ListStyle.Rows,
            sortBy = SortBy.DEFAULT,
            people = (0..10).map { i ->
                Person(
                    id = PersonId(i),
                    firstName = FirstName("$i First Name".toNonEmptyString()),
                    lastName = LastName("$i Last Name".toNonEmptyString()),
                    lastModified = "2012-10-03 12:45",
                )
            },
            deleteConfirmation = null,
            deleteWithNotesConfirmation = null,
            toastMessage = null,
        ),
        HomeUiState(
            isLoading = false,
            filter = "",
            listStyle = ListStyle.Rows,
            sortBy = SortBy.DEFAULT,
            people = emptyList(),
            deleteConfirmation = PersonId(1),
            deleteWithNotesConfirmation = null,
            toastMessage =
            ToastMessage(
                text = TextResource.fromText("User Message 1")
            ),
        ),
    ),
)