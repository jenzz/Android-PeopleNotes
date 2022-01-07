package com.jenzz.peoplenotes.feature.home.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.data.people.DeletePersonResult
import com.jenzz.peoplenotes.common.data.people.People
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.ui.TextResource
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.ext.mutableStateOf
import com.jenzz.peoplenotes.feature.home.data.HomeUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: HomeUseCases,
) : ViewModel() {

    var state by savedStateHandle.mutableStateOf(
        defaultValue = HomeUiState(
            isLoading = true,
            filter = "",
            listStyle = ListStyle.DEFAULT,
            sortBy = SortBy.DEFAULT,
            people = People(
                persons = emptyList(),
                totalCount = 0
            ),
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

    fun onListStyleChange(listStyle: ListStyle) {
        state = state.copy(listStyle = listStyle)
    }

    fun onFilterChange(filter: String) {
        state = state.copy(filter = filter)
        viewModelScope.launch {
            getPeople(filter = filter)
        }
    }

    fun onSortByChange(sortBy: SortBy) {
        state = state.copy(
            sortBy = sortBy,
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
                            text = TextResource.fromId(R.string.person_deleted, person.fullName)
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
                text = TextResource.fromId(R.string.person_deleted, person.fullName)
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
        sortBy: SortBy = state.sortBy,
        filter: String = state.filter,
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
