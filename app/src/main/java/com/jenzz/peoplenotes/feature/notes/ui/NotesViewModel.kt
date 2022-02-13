package com.jenzz.peoplenotes.feature.notes.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.common.ui.ToastMessageId
import com.jenzz.peoplenotes.common.ui.ToastMessageManager
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarState
import com.jenzz.peoplenotes.ext.saveableStateFlowOf
import com.jenzz.peoplenotes.feature.notes.data.NotesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    navArgs: NotesScreenNavArgs,
    useCases: NotesUseCases,
) : ViewModel() {

    val initialState = NotesUiState()

    private val toastMessageManager = ToastMessageManager()
    private val isLoading = MutableStateFlow(initialState.isLoading)
    private val searchBarState = savedStateHandle.saveableStateFlowOf(
        key = "searchBarState",
        initialValue = initialState.searchBarState,
    )
    private val notes = MutableStateFlow(initialState.notes)

    val state = combine(
        searchBarState.asStateFlow(),
        isLoading,
        notes,
        toastMessageManager.message,
        ::NotesUiState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = initialState,
    )

    init {
        searchBarState.asStateFlow()
            .flatMapLatest { state ->
                useCases.observeNotesWithPerson(
                    personId = navArgs.personId,
                    sortBy = state.sortBy.selected,
                    filter = state.searchTerm,
                )
            }
            .onEach { notes ->
                this.isLoading.value = false
                this.notes.value = notes.notes
            }
            .launchIn(viewModelScope)
    }

    fun onSearchBarStateChange(state: SearchBarState) {
        searchBarState.value = state
    }

    fun onToastShown(id: ToastMessageId) {
        viewModelScope.launch {
            toastMessageManager.clearMessage(id)
        }
    }
}
