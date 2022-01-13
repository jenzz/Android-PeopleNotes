package com.jenzz.peoplenotes.feature.person_details.ui

import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarUiState
import com.jenzz.peoplenotes.feature.person_details.data.PersonDetails

sealed class PersonDetailsUiState {

    abstract val searchBarState: SearchBarUiState
    abstract val toastMessage: ToastMessage?

    data class Loading(
        override val searchBarState: SearchBarUiState,
        override val toastMessage: ToastMessage?,
    ) : PersonDetailsUiState()

    data class Loaded(
        override val searchBarState: SearchBarUiState,
        override val toastMessage: ToastMessage?,
        val isLoading: Boolean,
        val personDetails: PersonDetails,
    ) : PersonDetailsUiState() {

        val isEmpty: Boolean =
            personDetails.notes.isEmpty

        val isEmptyFiltered: Boolean =
            isEmpty
                    && searchBarState.searchTerm.isNotEmpty()
                    && personDetails.notes.totalCount > 0
    }
}

val PersonDetailsUiState.isLoading: Boolean
    get() = this is PersonDetailsUiState.Loading
            || (this is PersonDetailsUiState.Loaded && isLoading)
