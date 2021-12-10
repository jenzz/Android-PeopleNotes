package com.jenzz.peoplenotes.feature.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.ui.common.MultiFloatingActionButton
import com.jenzz.peoplenotes.ui.common.MultiFloatingActionButtonItem

@Composable
fun HomeFloatingActionButton(
    onAddManuallyClick: () -> Unit,
    onFromContactsClick: () -> Unit,
) {
    val addManuallyItem = MultiFloatingActionButtonItem(
        id = 1,
        label = stringResource(id = R.string.add_manually),
        icon = Icons.Rounded.Add,
    )
    val fromContactsItem = MultiFloatingActionButtonItem(
        id = 2,
        label = stringResource(id = R.string.from_contacts),
        icon = Icons.Default.Person,
    )
    MultiFloatingActionButton(
        items = listOf(
            addManuallyItem,
            fromContactsItem,
        ),
        onItemClick = { item ->
            when (item.id) {
                addManuallyItem.id -> onAddManuallyClick()
                fromContactsItem.id -> onFromContactsClick()
            }
        }
    )
}
