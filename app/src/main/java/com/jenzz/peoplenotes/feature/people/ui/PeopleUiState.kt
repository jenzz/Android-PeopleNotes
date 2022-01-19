package com.jenzz.peoplenotes.feature.people.ui

import android.os.Parcelable
import com.jenzz.peoplenotes.common.data.people.People
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarState
import com.jenzz.peoplenotes.ext.PartialSavedState
import kotlinx.parcelize.Parcelize

data class PeopleUiState(
    val isLoading: Boolean,
    val people: People,
    val deleteConfirmation: PersonId?,
    val deleteWithNotesConfirmation: PersonId?,
    val toastMessage: ToastMessage?,
) : PartialSavedState<PeopleUiState, PeopleSavedState> {

    val isEmpty: Boolean =
        people.isEmpty

    val showActions: Boolean =
        !isLoading && !isEmpty

    fun isEmptyFiltered(searchBarState: SearchBarState): Boolean =
        isEmpty
                && searchBarState.searchTerm.isNotEmpty()
                && people.totalCount > 0

    override val savedState: PeopleSavedState =
        PeopleSavedState(
            deleteConfirmation = deleteConfirmation,
            deleteWithNotesConfirmation = deleteWithNotesConfirmation,
        )

    override fun mergeWithSavedState(savedState: PeopleSavedState): PeopleUiState =
        copy(
            deleteConfirmation = savedState.deleteConfirmation,
            deleteWithNotesConfirmation = savedState.deleteWithNotesConfirmation,
        )
}

@Parcelize
data class PeopleSavedState(
    val deleteConfirmation: PersonId?,
    val deleteWithNotesConfirmation: PersonId?,
) : Parcelable
