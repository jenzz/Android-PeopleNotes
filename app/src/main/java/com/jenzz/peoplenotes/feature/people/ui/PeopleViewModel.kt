package com.jenzz.peoplenotes.feature.people.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.data.people.DeletePersonResult
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.ui.TextResource
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.ToastMessageManager
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarState
import com.jenzz.peoplenotes.ext.combine
import com.jenzz.peoplenotes.ext.saveableStateFlowOf
import com.jenzz.peoplenotes.feature.people.data.PeopleUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: PeopleUseCases,
) : ViewModel() {

    val initialState = PeopleUiState()

    private val toastMessageManager = ToastMessageManager()
    private val loading = MutableStateFlow(true)
    private val searchBar = savedStateHandle.saveableStateFlowOf(
        key = "searchBar",
        initialValue = initialState.searchBarState
    )
    private val deleteConfirmation = MutableStateFlow<PersonId?>(null)
    private val deleteWithNotesConfirmation = MutableStateFlow<PersonId?>(null)
    private val people = searchBar.asStateFlow().flatMapLatest { state ->
        useCases.observePeople(
            sortBy = state.sortBy.selected,
            filter = state.searchTerm,
        )
    }

    val state =
        combine(
            searchBar.asStateFlow(),
            loading,
            people,
            deleteConfirmation,
            deleteWithNotesConfirmation,
            toastMessageManager.message,
        ) { searchBarState, _, people, deleteConfirmation, deleteWithNotesConfirmation, toastMessage ->
            PeopleUiState(
                searchBarState = searchBarState,
                isLoading = false,
                people = people,
                deleteConfirmation = deleteConfirmation,
                deleteWithNotesConfirmation = deleteWithNotesConfirmation,
                toastMessage = toastMessage,
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = initialState,
            )

    fun onSearchBarStateChange(state: SearchBarState) {
        searchBar.value = state
    }

    fun onDeleteRequest(person: Person) {
        deleteConfirmation.value = person.id
    }

    fun onDeleteCancel() {
        deleteConfirmation.value = null
    }

    fun onDeleteConfirm(person: Person) {
        loading.value = true
        deleteConfirmation.value = null
        viewModelScope.launch {
            when (useCases.deletePerson(person.id)) {
                is DeletePersonResult.RemainingNotesForPerson -> {
                    loading.value = false
                    deleteWithNotesConfirmation.value = person.id
                }
                is DeletePersonResult.Success -> {
                    loading.value = false
                    toastMessageManager.emitMessage(
                        ToastMessage(
                            text = TextResource.fromId(
                                id = R.string.person_deleted,
                                person.fullName,
                            )
                        )
                    )
                }
            }
        }
    }

    fun onDeleteWithNotes(person: Person) {
        loading.value = true
        deleteWithNotesConfirmation.value = null
        viewModelScope.launch {
            useCases.deletePersonWithNotes(person.id)
            loading.value = false
            toastMessageManager.emitMessage(
                ToastMessage(
                    text = TextResource.fromId(
                        id = R.string.person_deleted,
                        person.fullName,
                    )
                )
            )
        }
    }

    fun onDeleteWithNotesCancel() {
        deleteWithNotesConfirmation.value = null
    }

    fun onToastMessageShown(id: Long) {
        viewModelScope.launch {
            toastMessageManager.clearMessage(id)
        }
    }
}
