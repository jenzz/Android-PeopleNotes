package com.jenzz.peoplenotes.feature.home.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.jenzz.peoplenotes.ext.toNonEmptyString
import com.jenzz.peoplenotes.feature.home.data.Home
import com.jenzz.peoplenotes.feature.home.data.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAddPersonManuallyClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val context = LocalContext.current
    HomeContent(
        state = viewModel.state.value,
        onSortBy = { sortBy ->
            viewModel.onSortBy(sortBy)
            Toast.makeText(
                context,
                context.getString(R.string.sorted_by, sortBy),
                Toast.LENGTH_LONG
            ).show()
        },
        onAddPersonManuallyClick = onAddPersonManuallyClick,
        onSettingsClick = onSettingsClick,
    )
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onSortBy: (SortBy) -> Unit,
    onAddPersonManuallyClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            HomeTopAppBar(
                sortedBy = (state as? HomeUiState.Loaded)?.home?.sortedBy,
                onSortBy = onSortBy,
                onSettingsClick = onSettingsClick,
            )
        },
        floatingActionButton = {
            HomeFloatingActionButton(
                onAddPersonManuallyClick = onAddPersonManuallyClick,
                onImportFromContactsClick = {
                    Toast.makeText(
                        context,
                        "Import from contacts",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    ) {
        when (state) {
            is HomeUiState.Loading ->
                HomeLoading()
            is HomeUiState.Loaded ->
                HomeLoaded(home = state.home)
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
private fun HomeLoaded(home: Home) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(home.people) { person ->
            PersonRow(person)
        }
    }
}

@Composable
private fun PersonRow(person: Person) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Text(text = "Id: ${person.id.value}")
            Text(text = "${person.firstName.value} ${person.lastName.value}")
            Text(text = "Last modified: ${person.lastModified}")
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
                onSortBy = {},
                onAddPersonManuallyClick = {},
                onSettingsClick = {}
            )
        }
    }
}

class HomePreviewParameterProvider : CollectionPreviewParameterProvider<HomeUiState>(
    listOf(
        HomeUiState.Loading,
        HomeUiState.Loaded(
            Home(
                sortedBy = SortBy.LastModified,
                people = (0..10).map { i ->
                    Person(
                        id = PersonId(i),
                        firstName = FirstName("$i First Name".toNonEmptyString()),
                        lastName = LastName("$i Last Name".toNonEmptyString()),
                        lastModified = "$i Last Modified",
                    )
                },
            )
        ),
    )
)
