package com.jenzz.peoplenotes.feature.people.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.data.people.DeletePersonResult
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.data.people.PersonSimplified
import com.jenzz.peoplenotes.common.data.people.simplified
import com.jenzz.peoplenotes.common.ui.TextResource
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.ToastMessageId
import com.jenzz.peoplenotes.common.ui.ToastMessageManager
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarState
import com.jenzz.peoplenotes.ext.combine
import com.jenzz.peoplenotes.ext.saveableStateFlowOf
import com.jenzz.peoplenotes.feature.people.data.PeopleUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: PeopleUseCases,
) : ViewModel() {

    val initialState = PeopleUiState()

    private val toastMessageManager = ToastMessageManager()
    private val isLoading = MutableStateFlow(initialState.isLoading)
    private val searchBarState = savedStateHandle.saveableStateFlowOf(
        key = "searchBarState",
        initialValue = initialState.searchBarState,
    )
    private val people = MutableStateFlow(initialState.people)
    private val showDeleteConfirmation = MutableStateFlow(initialState.showDeleteConfirmation)
    private val showDeleteWithNotesConfirmation =
        MutableStateFlow(initialState.showDeleteWithNotesConfirmation)

    val state = combine(
        searchBarState,
        isLoading,
        people,
        showDeleteConfirmation,
        showDeleteWithNotesConfirmation,
        toastMessageManager.message,
        ::PeopleUiState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = initialState,
    )

    init {
        searchBarState
            .flatMapLatest { state ->
                useCases.observePeople(
                    sortBy = state.sortBy.selected,
                    filter = state.searchTerm,
                )
            }
            .onEach { filteredPeople ->
                isLoading.emit(false)
                people.emit(filteredPeople)
            }
            .launchIn(viewModelScope)
    }

    fun onSearchBarStateChange(state: SearchBarState) {
        searchBarState.tryEmit(state)
    }

    fun onDelete(person: PersonSimplified) {
        showDeleteConfirmation.tryEmit(person)
    }

    fun onDeleteCancel() {
        showDeleteConfirmation.tryEmit(null)
    }

    fun onDeleteConfirm(personId: PersonId) {
        viewModelScope.launch {
            isLoading.emit(true)
            showDeleteConfirmation.emit(null)
            when (val result = useCases.deletePerson(personId)) {
                is DeletePersonResult.RemainingNotesForPerson -> {
                    isLoading.emit(false)
                    showDeleteWithNotesConfirmation.emit(result.person.simplified())
                }
                is DeletePersonResult.Success -> {
                    isLoading.emit(false)
                    toastMessageManager.emitMessage(
                        ToastMessage(
                            text = TextResource.fromId(
                                id = R.string.person_deleted,
                                result.person.fullName
                            ),
                        )
                    )
                }
            }
        }
    }

    fun onDeleteWithNotesConfirm(personId: PersonId) {
        viewModelScope.launch {
            isLoading.emit(true)
            showDeleteWithNotesConfirmation.emit(null)
            val person = useCases.deletePersonWithNotes(personId)
            isLoading.emit(false)
            toastMessageManager.emitMessage(
                ToastMessage(
                    text = TextResource.fromId(
                        id = R.string.person_deleted,
                        person.fullName
                    ),
                )
            )
        }
    }

    fun onDeleteWithNotesCancel() {
        showDeleteWithNotesConfirmation.tryEmit(null)
    }

    fun onToastShown(id: ToastMessageId) {
        viewModelScope.launch {
            toastMessageManager.clearMessage(id)
        }
    }
}
