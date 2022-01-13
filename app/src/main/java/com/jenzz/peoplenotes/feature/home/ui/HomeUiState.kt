package com.jenzz.peoplenotes.feature.home.ui

import android.os.Parcelable
import com.jenzz.peoplenotes.common.data.people.People
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarUiState
import com.jenzz.peoplenotes.ext.PartialSavedState
import kotlinx.parcelize.Parcelize

data class HomeUiState(
    val isLoading: Boolean,
    val searchBarState: SearchBarUiState,
    val people: People,
    val deleteConfirmation: PersonId?,
    val deleteWithNotesConfirmation: PersonId?,
    val toastMessage: ToastMessage?,
) : PartialSavedState<HomeUiState, HomeSavedState> {

    val isEmpty: Boolean =
        people.isEmpty

    val isEmptyFiltered: Boolean =
        isEmpty && searchBarState.searchTerm.isNotEmpty() && people.totalCount > 0

    val showActions: Boolean =
        !isLoading && !isEmpty

    override val savedState: HomeSavedState =
        HomeSavedState(
            searchBarState = searchBarState,
            deleteConfirmation = deleteConfirmation,
            deleteWithNotesConfirmation = deleteWithNotesConfirmation,
        )

    override fun mergeWithSavedState(savedState: HomeSavedState): HomeUiState =
        copy(
            searchBarState = savedState.searchBarState,
            deleteConfirmation = savedState.deleteConfirmation,
            deleteWithNotesConfirmation = savedState.deleteWithNotesConfirmation,
        )
}

@Parcelize
data class HomeSavedState(
    val searchBarState: SearchBarUiState,
    val deleteConfirmation: PersonId?,
    val deleteWithNotesConfirmation: PersonId?,
) : Parcelable
