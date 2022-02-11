package com.jenzz.peoplenotes.feature.people.ui

import com.jenzz.peoplenotes.common.data.people.People
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.ui.ListStyle
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarState

data class PeopleUiState(
    val searchBarState: SearchBarState = SearchBarState(
        searchTerm = "",
        listStyle = ListStyle.DEFAULT,
        sortBy = PeopleSortBy.toSortByState(),
    ),
    val isLoading: Boolean = true,
    val people: People = People(),
    val deleteConfirmation: PersonId? = null,
    val deleteWithNotesConfirmation: PersonId? = null,
    val toastMessage: ToastMessage? = null,
) {

    val isEmpty: Boolean =
        people.isEmpty

    val showActions: Boolean =
        !isLoading && !isEmpty

    fun isEmptyFiltered(searchBarState: SearchBarState): Boolean =
        isEmpty
                && searchBarState.searchTerm.isNotEmpty()
                && people.totalCount > 0
}
