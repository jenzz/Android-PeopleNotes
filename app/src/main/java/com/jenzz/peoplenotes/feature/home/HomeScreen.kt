package com.jenzz.peoplenotes.feature.home

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.ui.theme.PeopleNotesTheme

@Composable
fun HomeScreen(
    onSettingsClick: () -> Unit,
) {
    val context = LocalContext.current
    var sortedBy by remember { mutableStateOf(SortBy.LastModified) }
    HomeContent(
        sortedBy = sortedBy,
        onSortBy = { sortBy ->
            sortedBy = sortBy
            Toast.makeText(
                context,
                context.getString(R.string.sorted_by, sortBy),
                Toast.LENGTH_LONG
            ).show()
        },
        onSettingsClick = onSettingsClick,
    )
}

@Composable
private fun HomeContent(
    sortedBy: SortBy,
    onSortBy: (SortBy) -> Unit,
    onSettingsClick: () -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            HomeTopAppBar(
                sortedBy = sortedBy,
                onSortBy = onSortBy,
                onSettingsClick = onSettingsClick,
            )
        },
        floatingActionButton = {
            HomeFloatingActionButton(
                onAddManuallyClick = {
                    Toast.makeText(
                        context,
                        "Add Manually",
                        Toast.LENGTH_LONG
                    ).show()
                },
                onFromContactsClick = {
                    Toast.makeText(
                        context,
                        "From Contacts",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    ) {
        LazyColumn {
            items(50) {
                Text("Item $it")
            }
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
fun HomeScreenPreview() {
    PeopleNotesTheme {
        Surface {
            HomeScreen(
                onSettingsClick = {}
            )
        }
    }
}
