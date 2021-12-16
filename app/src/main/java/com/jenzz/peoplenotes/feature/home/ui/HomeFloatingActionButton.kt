package com.jenzz.peoplenotes.feature.home.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.widgets.MultiFloatingActionButton
import com.jenzz.peoplenotes.common.ui.widgets.MultiFloatingActionButtonItem

@Composable
fun HomeFloatingActionButton(
    onAddPersonManuallyClick: () -> Unit,
    onImportFromContactsClick: () -> Unit,
) {
    val addPersonManuallyItem = MultiFloatingActionButtonItem(
        id = 1,
        label = stringResource(id = R.string.add_person_manually),
        icon = Icons.Rounded.Add,
    )
    val importFromContactsItem = MultiFloatingActionButtonItem(
        id = 2,
        label = stringResource(id = R.string.import_from_contacts),
        icon = Icons.Default.Person,
    )
    MultiFloatingActionButton(
        items = listOf(
            addPersonManuallyItem,
            importFromContactsItem,
        ),
        onItemClick = { item ->
            when (item.id) {
                addPersonManuallyItem.id -> onAddPersonManuallyClick()
                importFromContactsItem.id -> onImportFromContactsClick()
            }
        }
    )
}
