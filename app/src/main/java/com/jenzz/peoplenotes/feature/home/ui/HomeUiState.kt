package com.jenzz.peoplenotes.feature.home.ui

import android.os.Parcelable
import com.jenzz.peoplenotes.common.data.people.People
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.ext.PartialSavedState
import kotlinx.parcelize.Parcelize

data class HomeUiState(
    val isLoading: Boolean,
    val filter: String,
    val listStyle: ListStyle,
    val sortBy: SortBy,
    val people: People,
    val deleteConfirmation: PersonId?,
    val deleteWithNotesConfirmation: PersonId?,
    val toastMessage: ToastMessage?,
) : PartialSavedState<HomeUiState, HomeSavedState> {

    val isEmpty: Boolean =
        people.isEmpty

    val isEmptyFiltered: Boolean =
        isEmpty && filter.isNotEmpty() && people.totalCount > 0

    val showActions: Boolean =
        !isLoading && !isEmpty

    override val savedState: HomeSavedState =
        HomeSavedState(
            filter = filter,
            listStyle = listStyle,
            sortBy = sortBy,
            deleteConfirmation = deleteConfirmation,
            deleteWithNotesConfirmation = deleteWithNotesConfirmation,
        )

    override fun mergeWithSavedState(savedState: HomeSavedState): HomeUiState =
        copy(
            filter = savedState.filter,
            listStyle = savedState.listStyle,
            sortBy = savedState.sortBy,
            deleteConfirmation = savedState.deleteConfirmation,
            deleteWithNotesConfirmation = savedState.deleteWithNotesConfirmation,
        )
}

@Parcelize
data class HomeSavedState(
    val filter: String,
    val listStyle: ListStyle,
    val sortBy: SortBy,
    val deleteConfirmation: PersonId?,
    val deleteWithNotesConfirmation: PersonId?,
) : Parcelable
