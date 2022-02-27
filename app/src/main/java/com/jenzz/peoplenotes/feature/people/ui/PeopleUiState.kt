package com.jenzz.peoplenotes.feature.people.ui

import com.jenzz.peoplenotes.common.data.people.People
import com.jenzz.peoplenotes.common.data.people.PersonSimplified
import com.jenzz.peoplenotes.common.ui.ListStyle
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarInput

data class PeopleUiState(
    val searchBar: SearchBarInput = SearchBarInput(
        searchTerm = "",
        listStyle = ListStyle.DEFAULT,
        sortBy = PeopleSortBy.toSortByState(),
    ),
    val isLoading: Boolean = true,
    val people: People = People(),
    val showDeleteConfirmation: PersonSimplified? = null,
    val showDeleteWithNotesConfirmation: PersonSimplified? = null,
    val toastMessage: ToastMessage? = null,
) {

    val isEmpty: Boolean =
        people.isEmpty

    val showActions: Boolean =
        !isLoading && !isEmpty

    fun isEmptyFiltered(searchBar: SearchBarInput): Boolean =
        isEmpty
                && searchBar.searchTerm.isNotEmpty()
                && people.totalCount > 0
}
