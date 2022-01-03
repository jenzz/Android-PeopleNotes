package com.jenzz.peoplenotes.feature.home.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
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
        onDelete = { person ->
            viewModel.onDelete(person)
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
    onClick: (Person) -> Unit,
    onDelete: (Person) -> Unit,
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
            modifier = Modifier.size(48.dp),
            painter = painterResource(id = R.drawable.ic_people),
            contentDescription = stringResource(id = R.string.empty_people),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(id = R.string.empty_people),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.caption,
        )
    }
}

@Composable
private fun HomeLoaded(
    state: HomeUiState,
    onClick: (Person) -> Unit,
    onDelete: (Person) -> Unit,
) {
    when (state.listStyle) {
        ListStyle.Rows ->
            HomeLoadedRows(
                people = state.people,
                onClick = onClick,
                onDelete = onDelete,
            )
        ListStyle.Grid ->
            HomeLoadedGrid(
                people = state.people,
                onClick = onClick,
                onDelete = onDelete,
            )
    }
}

@Composable
private fun HomeLoadedRows(
    people: List<Person>,
    onClick: (Person) -> Unit,
    onDelete: (Person) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(people) { person ->
            PersonRow(
                modifier = Modifier.padding(4.dp),
                person = person,
                onClick = onClick,
                onDelete = onDelete,
            )
        }
    }
}

@Composable
private fun HomeLoadedGrid(
    people: List<Person>,
    onClick: (Person) -> Unit,
    onDelete: (Person) -> Unit,
) {
    StaggeredVerticalGrid(
        modifier = Modifier.padding(4.dp),
        maxColumnWidth = 220.dp,
    ) {
        people.forEach { person ->
            PersonGrid(
                modifier = Modifier.padding(8.dp),
                person = person,
                onClick = onClick,
                onDelete = onDelete,
            )
        }
    }
}

@Composable
private fun PersonRow(
    modifier: Modifier = Modifier,
    person: Person,
    onClick: (Person) -> Unit,
    onDelete: (Person) -> Unit,
) {
    PersonCard(
        modifier = modifier,
        person = person,
        onClick = onClick,
        onDelete = onDelete,
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
    onClick: (Person) -> Unit,
    onDelete: (Person) -> Unit,
) {
    PersonCard(
        modifier = modifier,
        person = person,
        onClick = onClick,
        onDelete = onDelete,
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
    onClick: (Person) -> Unit,
    onDelete: (Person) -> Unit,
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
                onLongClick = { selected = true }
            )
        ) {
            content()
            PersonDropDownMenu(
                selected = selected,
                onDismissRequest = { selected = false },
                onDelete = { person ->
                    selected = false
                    onDelete(person)
                },
                person = person
            )
        }
    }
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
    selected: Boolean,
    onDismissRequest: () -> Unit,
    onDelete: (Person) -> Unit,
    person: Person,
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
                    lastModified = "2012-10-03 12:45",
                )
            },
        ),
    )
)
