package com.jenzz.peoplenotes.feature.people.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.widgets.MultiFloatingActionButton
import com.jenzz.peoplenotes.common.ui.widgets.MultiFloatingActionButtonItem
import com.jenzz.peoplenotes.common.ui.widgets.MultiFloatingActionButtonState

@Composable
fun PeopleFloatingActionButton(
    state: MultiFloatingActionButtonState,
    onStateChange: (MultiFloatingActionButtonState) -> Unit,
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
        icon = Icons.Rounded.Person,
    )
    MultiFloatingActionButton(
        state = state,
        onStateChange = onStateChange,
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
