package com.jenzz.peoplenotes.feature.people.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.data.people.DeletePersonResult
import com.jenzz.peoplenotes.common.data.people.People
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.ui.SortBy
import com.jenzz.peoplenotes.common.ui.TextResource
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarState
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

    var state by savedStateHandle.mutableStateOf(
        defaultValue = PeopleUiState(
            isLoading = true,
            people = People.DEFAULT,
            deleteConfirmation = null,
            deleteWithNotesConfirmation = null,
            toastMessage = null,
        )
    )
        private set

    private lateinit var searchBarState: SearchBarState

    fun init(searchBarState: SearchBarState) {
        this.searchBarState = searchBarState
        viewModelScope.launch {
            observePeople()
        }
    }

    fun onSearchTermChange(searchTerm: String) {
        viewModelScope.launch {
            observePeople(filter = searchTerm)
        }
    }

    fun onSortByChange(sortBy: SortBy) {
        viewModelScope.launch {
            observePeople(sortBy = sortBy)
        }
        state = state.copy(
            toastMessage = ToastMessage(
                text = TextResource.fromId(R.string.sorted_by, sortBy.label)
            ),
        )
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

    private suspend fun observePeople(
        sortBy: SortBy = searchBarState.sortBy.selected,
        filter: String = searchBarState.searchTerm,
    ) {
        state = state.copy(isLoading = true)
        useCases
            .observePeople(sortBy, filter)
            .collect { people ->
                state = state.copy(
                    isLoading = false,
                    people = people,
                )
            }
    }
}
