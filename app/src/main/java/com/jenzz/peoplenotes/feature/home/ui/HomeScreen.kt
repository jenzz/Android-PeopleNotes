package com.jenzz.peoplenotes.feature.home.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.jenzz.peoplenotes.common.ui.theme.PeopleNotesTheme
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
        onPersonClick = { /* TODO JD */ },
        onDeletePerson = { person ->
            viewModel.onDeletePerson(person)
            context.showLongToast(R.string.person_deleted)
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
    onPersonClick: (Person) -> Unit,
    onDeletePerson: (Person) -> Unit,
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
                    onPersonClick = onPersonClick,
                    onDeletePerson = onDeletePerson,
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
    onPersonClick: (Person) -> Unit,
    onDeletePerson: (Person) -> Unit,
) {
    when (state.listStyle) {
        ListStyle.Rows ->
            HomeLoadedRows(
                people = state.people,
                onPersonClick = onPersonClick,
                onDeletePerson = onDeletePerson,
            )
        ListStyle.Grid ->
            HomeLoadedGrid(
                people = state.people,
                onPersonClick = onPersonClick,
                onDeletePerson = onDeletePerson,
            )
    }
}

@Composable
private fun HomeLoadedRows(
    people: List<Person>,
    onPersonClick: (Person) -> Unit,
    onDeletePerson: (Person) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(people) { person ->
            PersonRow(
                person = person,
                onClick = onPersonClick,
                onDeletePerson = onDeletePerson,
            )
        }
    }
}

@Composable
private fun HomeLoadedGrid(
    people: List<Person>,
    columns: Int = 2,
    onPersonClick: (Person) -> Unit,
    onDeletePerson: (Person) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(4.dp),
    ) {
        items(people) { person ->
            Row {
                for (columnIndex in 0 until columns) {
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(1f, fill = true),
                    ) {
                        PersonRow(
                            person = person,
                            onClick = onPersonClick,
                            onDeletePerson = onDeletePerson,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonRow(
    person: Person,
    onClick: (Person) -> Unit,
    onDeletePerson: (Person) -> Unit,
) {
    var selected by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .combinedClickable(
                onClick = { onClick(person) },
                onLongClick = { selected = true }
            ),
        border = if (selected)
            BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface
            ) else null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Text(text = "Id: ${person.id.value}")
            Text(text = "${person.firstName.value} ${person.lastName.value}")
            Text(text = "Last modified: ${person.lastModified}")
        }
        PersonDropDownMenu(
            selected = selected,
            onDismissRequest = { selected = false },
            onDeletePerson = { person ->
                selected = false
                onDeletePerson(person)
            },
            person = person
        )
    }
}

@Composable
private fun PersonDropDownMenu(
    selected: Boolean,
    onDismissRequest: () -> Unit,
    onDeletePerson: (Person) -> Unit,
    person: Person,
) {
    DropdownMenu(
        expanded = selected,
        onDismissRequest = onDismissRequest,
    ) {
        DropdownMenuItem(onClick = { onDeletePerson(person) }) {
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
                onPersonClick = {},
                onDeletePerson = {},
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
            people = emptyList(),
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
                    lastModified = "$i Last Modified",
                )
            },
        ),
    )
)
