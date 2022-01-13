package com.jenzz.peoplenotes.feature.person_details.ui

import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarUiState
import com.jenzz.peoplenotes.feature.person_details.data.PersonDetails

sealed class PersonDetailsUiState {

    abstract val searchBarState: SearchBarUiState
    abstract val showActions: Boolean

    data class InitialLoad(
        override val searchBarState: SearchBarUiState,
    ) : PersonDetailsUiState() {

        override val showActions: Boolean = false
    }

    data class Loaded(
        override val searchBarState: SearchBarUiState,
        val isLoading: Boolean,
        val personDetails: PersonDetails,
        val toastMessage: ToastMessage?,
    ) : PersonDetailsUiState() {

        val isEmpty: Boolean =
            personDetails.notes.isEmpty

        val isEmptyFiltered: Boolean =
            isEmpty
                    && searchBarState.searchTerm.isNotEmpty()
                    && personDetails.notes.totalCount > 0

        override val showActions: Boolean =
            !isLoading && !isEmpty
    }
}

val PersonDetailsUiState.isLoading: Boolean
    get() = this is PersonDetailsUiState.InitialLoad
            || (this is PersonDetailsUiState.Loaded && isLoading)
