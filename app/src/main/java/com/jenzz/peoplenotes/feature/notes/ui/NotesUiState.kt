package com.jenzz.peoplenotes.feature.notes.ui

import com.jenzz.peoplenotes.common.ui.ListStyle
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarState
import com.jenzz.peoplenotes.feature.notes.data.Notes

sealed class NotesUiState {

    open val searchBarState: SearchBarState = SearchBarState(
        searchTerm = "",
        listStyle = ListStyle.DEFAULT,
        sortBy = NotesSortBy.toSortByState(),
    )
    abstract val showActions: Boolean
    abstract val notesCount: Int
    abstract val toastMessage: ToastMessage?

    object InitialLoad : NotesUiState() {

        override val showActions: Boolean = false
        override val notesCount: Int = 0
        override val toastMessage: ToastMessage? = null
    }

    data class Loaded(
        override val searchBarState: SearchBarState,
        override val toastMessage: ToastMessage?,
        val isLoading: Boolean,
        val notes: Notes,
    ) : NotesUiState() {

        val isEmpty: Boolean =
            notes.isEmpty

        override val showActions: Boolean =
            !isLoading && !isEmpty

        override val notesCount: Int = notes.notes.items.size

        fun isEmptyFiltered(searchBarState: SearchBarState): Boolean =
            isEmpty
                    && searchBarState.searchTerm.isNotEmpty()
                    && notes.notes.totalCount > 0
    }
}

val NotesUiState.isLoading: Boolean
    get() = this is NotesUiState.InitialLoad
            || (this is NotesUiState.Loaded && isLoading)
