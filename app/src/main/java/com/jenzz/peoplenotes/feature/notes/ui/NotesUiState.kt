package com.jenzz.peoplenotes.feature.notes.ui

import com.jenzz.peoplenotes.common.data.notes.NoteId
import com.jenzz.peoplenotes.common.ui.ListStyle
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarInput
import com.jenzz.peoplenotes.feature.notes.data.Notes

data class NotesUiState(
    val searchBar: SearchBarInput = SearchBarInput(
        searchTerm = "",
        listStyle = ListStyle.DEFAULT,
        sortBy = NotesSortBy.toSortByState(),
    ),
    val isLoading: Boolean = true,
    val notes: Notes = Notes(),
    val showDeleteConfirmation: NoteId? = null,
    val toastMessage: ToastMessage? = null,
) {

    val isEmpty: Boolean = notes.isEmpty

    val showActions: Boolean = !isLoading && !isEmpty

    val notesCount: Int = notes.list.size

    fun isEmptyFiltered(searchBar: SearchBarInput): Boolean =
        isEmpty && searchBar.searchTerm.isNotEmpty() && notes.totalCount > 0
}
