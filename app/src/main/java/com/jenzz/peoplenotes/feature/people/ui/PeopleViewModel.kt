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
    private val searchBarState = savedStateHandle.saveableStateFlowOf(
        key = "searchBar",
        initialValue = initialState.searchBarState
    )
    private val showDeleteConfirmation = MutableStateFlow<PersonId?>(null)
    private val showDeleteWithNotesConfirmation = MutableStateFlow<PersonId?>(null)
    private val people = searchBarState.asStateFlow().flatMapLatest { state ->
        useCases.observePeople(
            sortBy = state.sortBy.selected,
            filter = state.searchTerm,
        )
    }

    val state = combine(
        searchBarState.asStateFlow(),
        loading,
        people,
        showDeleteConfirmation,
        showDeleteWithNotesConfirmation,
        toastMessageManager.message,
        ::PeopleUiState
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = initialState,
    )

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
        loading.value = true
        showDeleteConfirmation.value = null
        viewModelScope.launch {
            when (useCases.deletePerson(personId)) {
                is DeletePersonResult.RemainingNotesForPerson -> {
                    loading.value = false
                    showDeleteWithNotesConfirmation.value = personId
                }
                is DeletePersonResult.Success -> {
                    loading.value = false
                    toastMessageManager.emitMessage(
                        ToastMessage(text = TextResource.fromId(R.string.person_deleted))
                    )
                }
            }
        }
    }

    fun onDeleteWithNotesConfirm(personId: PersonId) {
        loading.value = true
        showDeleteWithNotesConfirmation.value = null
        viewModelScope.launch {
            useCases.deletePersonWithNotes(personId)
            loading.value = false
            toastMessageManager.emitMessage(
                ToastMessage(text = TextResource.fromId(id = R.string.person_deleted))
            )
        }
    }

    fun onDeleteWithNotesCancel() {
        showDeleteWithNotesConfirmation.value = null
    }

    fun onToastMessageShown(id: ToastMessageId) {
        viewModelScope.launch {
            toastMessageManager.clearMessage(id)
        }
    }
}
