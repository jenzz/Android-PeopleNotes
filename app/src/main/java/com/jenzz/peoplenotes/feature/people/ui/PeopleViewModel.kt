package com.jenzz.peoplenotes.feature.people.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.data.people.DeletePersonResult
import com.jenzz.peoplenotes.common.data.people.People
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.ui.*
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarState
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarUiState
import com.jenzz.peoplenotes.ext.mutableStateOf
import com.jenzz.peoplenotes.feature.people.data.PeopleUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: PeopleUseCases,
) : ViewModel() {

    private val searchBarState: SearchBarState =
        SearchBarState(
            initialState = SearchBarUiState(
                searchTerm = "",
                listStyle = ListStyle.DEFAULT,
                sortByState = SortByUiState(
                    items = PeopleSortBy.toSortBy()
                )
            )
        )
    var state by savedStateHandle.mutableStateOf(
        defaultValue = PeopleUiState(
            isLoading = true,
            searchBarState = searchBarState.state,
            people = People.DEFAULT,
            deleteConfirmation = null,
            deleteWithNotesConfirmation = null,
            toastMessage = null,
        )
    )
        private set

    init {
        viewModelScope.launch {
            getPeople()
        }
    }

    fun onSearchTermChange(searchTerm: String) {
        state = state.copy(searchBarState = searchBarState.onSearchTermChange(searchTerm))
        viewModelScope.launch {
            getPeople(filter = searchTerm)
        }
    }

    fun onListStyleChange(listStyle: ListStyle) {
        state = state.copy(searchBarState = searchBarState.onListStyleChange(listStyle))
    }

    fun onSortByChange(sortBy: SortBy) {
        state = state.copy(
            searchBarState = searchBarState.onSortByChange(sortBy),
            toastMessage = ToastMessage(
                text = TextResource.fromId(R.string.sorted_by, sortBy.label)
            ),
        )
        viewModelScope.launch {
            getPeople(sortBy = sortBy)
        }
    }

    fun onDeleteRequest(person: Person) {
        state = state.copy(deleteConfirmation = person.id)
    }

    fun onDeleteCancel() {
        state = state.copy(deleteConfirmation = null)
    }

    fun onDeleteConfirm(person: Person) {
        state = state.copy(
            isLoading = true,
            deleteConfirmation = null,
        )
        viewModelScope.launch {
            state = when (useCases.deletePerson(person.id)) {
                is DeletePersonResult.RemainingNotesForPerson ->
                    state.copy(
                        isLoading = false,
                        deleteWithNotesConfirmation = person.id,
                    )
                is DeletePersonResult.Success -> {
                    state.copy(
                        isLoading = false,
                        toastMessage = ToastMessage(
                            text = TextResource.fromId(
                                id = R.string.person_deleted,
                                TextResource.fromText(person.fullName),
                            )
                        ),
                    )
                }
            }
        }
    }

    fun onDeleteWithNotes(person: Person) {
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            useCases.deletePersonWithNotes(person.id)
        }
        state = state.copy(
            isLoading = false,
            toastMessage = ToastMessage(
                text = TextResource.fromId(
                    id = R.string.person_deleted,
                    TextResource.fromText(person.fullName),
                )
            ),
        )
    }

    fun onDeleteWithNotesCancel() {
        state = state.copy(deleteWithNotesConfirmation = null)
    }

    fun onToastMessageShown() {
        state = state.copy(toastMessage = null)
    }

    private suspend fun getPeople(
        sortBy: SortBy = state.searchBarState.sortByState.selected,
        filter: String = state.searchBarState.searchTerm,
    ) {
        state = state.copy(isLoading = true)
        useCases
            .getPeople(sortBy, filter)
            .collect { people ->
                state = state.copy(
                    isLoading = false,
                    people = people,
                )
            }
    }
}
