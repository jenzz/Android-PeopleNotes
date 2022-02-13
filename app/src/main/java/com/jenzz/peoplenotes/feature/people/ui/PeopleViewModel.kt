package com.jenzz.peoplenotes.feature.people.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.data.people.DeletePersonResult
import com.jenzz.peoplenotes.common.data.people.PersonId
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
        searchBarState.asStateFlow(),
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
        searchBarState.asStateFlow()
            .flatMapLatest { state ->
                useCases.observePeople(
                    sortBy = state.sortBy.selected,
                    filter = state.searchTerm,
                )
            }
            .onEach { people ->
                this.isLoading.value = false
                this.people.value = people
            }
            .launchIn(viewModelScope)
    }

    fun onSearchBarStateChange(state: SearchBarState) {
        searchBarState.value = state
    }

    fun onDelete(personId: PersonId) {
        showDeleteConfirmation.value = personId
    }

    fun onDeleteCancel() {
        showDeleteConfirmation.value = null
    }

    fun onDeleteConfirm(personId: PersonId) {
        isLoading.value = true
        showDeleteConfirmation.value = null
        viewModelScope.launch {
            when (useCases.deletePerson(personId)) {
                is DeletePersonResult.RemainingNotesForPerson -> {
                    isLoading.value = false
                    showDeleteWithNotesConfirmation.value = personId
                }
                is DeletePersonResult.Success -> {
                    isLoading.value = false
                    toastMessageManager.emitMessage(
                        ToastMessage(text = TextResource.fromId(R.string.person_deleted))
                    )
                }
            }
        }
    }

    fun onDeleteWithNotesConfirm(personId: PersonId) {
        isLoading.value = true
        showDeleteWithNotesConfirmation.value = null
        viewModelScope.launch {
            useCases.deletePersonWithNotes(personId)
            isLoading.value = false
            toastMessageManager.emitMessage(
                ToastMessage(text = TextResource.fromId(id = R.string.person_deleted))
            )
        }
    }

    fun onDeleteWithNotesCancel() {
        showDeleteWithNotesConfirmation.value = null
    }

    fun onToastShown(id: ToastMessageId) {
        viewModelScope.launch {
            toastMessageManager.clearMessage(id)
        }
    }
}
