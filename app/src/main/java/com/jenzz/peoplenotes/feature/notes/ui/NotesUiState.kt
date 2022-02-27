package com.jenzz.peoplenotes.feature.notes.ui

import com.jenzz.peoplenotes.common.data.notes.NoteId
import com.jenzz.peoplenotes.common.data.notes.NotesList
import com.jenzz.peoplenotes.common.ui.ListStyle
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarState

data class NotesUiState(
    val searchBarState: SearchBarState = SearchBarState(
        searchTerm = "",
        listStyle = ListStyle.DEFAULT,
        sortBy = NotesSortBy.toSortByState(),
    ),
    val isLoading: Boolean = true,
    val notes: NotesList = NotesList(),
    val showDeleteConfirmation: NoteId? = null,
    val toastMessage: ToastMessage? = null,
) {

    val isEmpty: Boolean = notes.isEmpty

    val showActions: Boolean = !isLoading && !isEmpty

    val notesCount: Int = notes.items.size

    fun isEmptyFiltered(searchBarState: SearchBarState): Boolean =
        isEmpty && searchBarState.searchTerm.isNotEmpty() && notes.totalCount > 0
}
