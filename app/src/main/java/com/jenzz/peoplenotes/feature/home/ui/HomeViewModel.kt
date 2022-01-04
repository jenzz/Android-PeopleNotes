package com.jenzz.peoplenotes.feature.home.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.data.people.DeletePersonResult
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.ui.TextResource
import com.jenzz.peoplenotes.common.ui.UserMessage
import com.jenzz.peoplenotes.common.ui.UserMessageId
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
            people = emptyList(),
            showDeleteConfirmation = null,
            showDeleteWithNotesConfirmation = null,
            userMessages = emptyList(),
        )
    )
        private set

    init {
        viewModelScope.launch {
            useCases
                .getPeople(
                    sortBy = state.sortBy,
                    filter = state.filter
                )
                .collect { people ->
                    state = state.copy(
                        isLoading = false,
                        people = people,
                    )
                }
        }
    }

    fun onListStyleChanged(listStyle: ListStyle) {
        state = state.copy(listStyle = listStyle)
    }

    fun onFilterChanged(filter: String) {
        state = state.copy(filter = filter)
        viewModelScope.launch {
            val sortBy = checkNotNull(state.sortBy) { "Missing required sort order." }
            getPeople(sortBy = sortBy, filter = filter)
        }
    }

    fun onSortByChanged(sortBy: SortBy) {
        state = state.copy(
            sortBy = sortBy,
            userMessages = state.userMessages + UserMessage(
                text = TextResource.fromId(R.string.sorted_by, sortBy.label)
            )
        )
        viewModelScope.launch {
            getPeople(sortBy = sortBy, filter = "")
        }
    }

    fun onDeleteRequested(person: Person) {
        state = state.copy(showDeleteConfirmation = person.id)
    }

    fun onDeleteCancelled() {
        state = state.copy(showDeleteConfirmation = null)
    }

    fun onDeleteConfirmed(person: Person) {
        state = state.copy(
            isLoading = true,
            showDeleteConfirmation = null,
        )
        viewModelScope.launch {
            state = when (useCases.deletePerson(person.id)) {
                is DeletePersonResult.RemainingNotesForPerson ->
                    state.copy(
                        isLoading = false,
                        showDeleteWithNotesConfirmation = person.id,
                    )
                is DeletePersonResult.Success -> {
                    state.copy(
                        isLoading = false,
                        userMessages = state.userMessages + UserMessage(
                            text = TextResource.fromId(R.string.person_deleted, person.fullName)
                        )
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
        state = state.copy(isLoading = false)
    }

    fun onDeleteWithNotesCancelled() {
        state = state.copy(showDeleteWithNotesConfirmation = null)
    }

    fun onUserMessageShown(messageId: UserMessageId) {
        val messages = state.userMessages.filterNot { message -> message.id == messageId }
        state = state.copy(userMessages = messages)
    }

    private suspend fun getPeople(sortBy: SortBy, filter: String) {
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
