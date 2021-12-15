package com.jenzz.peoplenotes.feature.home.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.theme.PeopleNotesTheme

@Composable
fun HomeScreen(
    onSettingsClick: () -> Unit,
) {
    val context = LocalContext.current
    var sortedBy by rememberSaveable { mutableStateOf(SortBy.LastModified) }
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
private fun HomeContentPreview() {
    PeopleNotesTheme {
        Surface {
            HomeContent(
                sortedBy = SortBy.LastModified,
                onSortBy = {},
                onSettingsClick = {}
            )
        }
    }
}
