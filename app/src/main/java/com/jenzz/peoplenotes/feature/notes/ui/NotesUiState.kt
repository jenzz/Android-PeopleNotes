package com.jenzz.peoplenotes.feature.notes.ui

import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarUiState
import com.jenzz.peoplenotes.feature.notes.data.Notes

sealed class NotesUiState {

    abstract val searchBarState: SearchBarUiState
    abstract val showActions: Boolean

    data class InitialLoad(
        override val searchBarState: SearchBarUiState,
    ) : NotesUiState() {

        override val showActions: Boolean = false
    }

    data class Loaded(
        override val searchBarState: SearchBarUiState,
        val isLoading: Boolean,
        val notes: Notes,
        val toastMessage: ToastMessage?,
    ) : NotesUiState() {

        val isEmpty: Boolean =
            notes.isEmpty

        val isEmptyFiltered: Boolean =
            isEmpty
                    && searchBarState.searchTerm.isNotEmpty()
                    && notes.notes.totalCount > 0

        override val showActions: Boolean =
            !isLoading && !isEmpty
    }
}

val NotesUiState.isLoading: Boolean
    get() = this is NotesUiState.InitialLoad
            || (this is NotesUiState.Loaded && isLoading)
