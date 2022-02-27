package com.jenzz.peoplenotes.feature.notes.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.data.notes.NoteId
import com.jenzz.peoplenotes.common.ui.TextResource
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.ToastMessageId
import com.jenzz.peoplenotes.common.ui.ToastMessageManager
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarInput
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarState
import com.jenzz.peoplenotes.feature.notes.data.NotesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    navArgs: NotesScreenNavArgs,
    private val useCases: NotesUseCases,
) : ViewModel() {

    val initialState = NotesUiState()

    private val toastMessageManager = ToastMessageManager()
    private val isLoading = MutableStateFlow(initialState.isLoading)
    private val searchBar = SearchBarState(savedStateHandle, initialState.searchBar)
    private val notes = MutableStateFlow(initialState.notes)
    private val showDeleteConfirmation = MutableStateFlow(initialState.showDeleteConfirmation)

    val state = combine(
        searchBar.state,
        isLoading,
        notes,
        showDeleteConfirmation,
        toastMessageManager.message,
        ::NotesUiState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = initialState,
    )

    init {
        searchBar
            .state
            .onEach {
                isLoading.emit(true)
            }
            .flatMapLatest { state ->
                useCases.observeNotesWithPerson(
                    personId = navArgs.personId,
                    sortBy = state.sortBy.selected,
                    filter = state.searchTerm,
                )
            }
            .onEach { newNotes ->
                isLoading.emit(false)
                notes.emit(newNotes)
            }
            .launchIn(viewModelScope)

        searchBar
            .sortBy
            .drop(1) // Do not notify on initial load.
            .onEach { sortBy ->
                toastMessageManager.emitMessage(
                    message = ToastMessage(
                        text = TextResource.fromId(
                            id = R.string.sorted_by,
                            sortBy.label
                        )
                    )
                )
            }
            .launchIn(viewModelScope)
    }

    fun onSearchBarStateChange(state: SearchBarInput) {
        searchBar.onStateChange(state)
    }

    fun onDelete(noteId: NoteId) {
        showDeleteConfirmation.tryEmit(noteId)
    }

    fun onDeleteCancel() {
        showDeleteConfirmation.tryEmit(null)
    }

    fun onDeleteConfirm(noteId: NoteId) {
        viewModelScope.launch {
            isLoading.emit(true)
            showDeleteConfirmation.emit(null)
            useCases.deleteNote(noteId)
            isLoading.emit(false)
            toastMessageManager.emitMessage(
                message = ToastMessage(text = TextResource.fromId(R.string.note_deleted))
            )
        }
    }

    fun onToastShown(id: ToastMessageId) {
        viewModelScope.launch {
            toastMessageManager.clearMessage(id)
        }
    }
}
