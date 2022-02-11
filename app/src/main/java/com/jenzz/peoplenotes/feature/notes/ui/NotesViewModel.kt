package com.jenzz.peoplenotes.feature.notes.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.ToastMessageManager
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarState
import com.jenzz.peoplenotes.ext.saveableStateFlowOf
import com.jenzz.peoplenotes.feature.notes.data.Notes
import com.jenzz.peoplenotes.feature.notes.data.NotesUseCases
import com.jenzz.peoplenotes.feature.notes.ui.NotesUiState.InitialLoad
import com.jenzz.peoplenotes.feature.notes.ui.NotesUiState.Loaded
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    navArgs: NotesScreenNavArgs,
    useCases: NotesUseCases,
) : ViewModel() {

    val initialState = InitialLoad

    private val toastMessageManager = ToastMessageManager()
    private val searchBar = savedStateHandle.saveableStateFlowOf(
        key = "searchBar",
        initialValue = initialState.searchBarState
    )
    private val notes = searchBar.asStateFlow().flatMapLatest { state ->
        useCases.observeNotesWithPerson(
            personId = navArgs.personId,
            sortBy = state.sortBy.selected,
            filter = state.searchTerm,
        )
    }

    val state =
        combine<SearchBarState, Notes, ToastMessage?, NotesUiState>(
            searchBar.asStateFlow(),
            notes,
            toastMessageManager.message,
        ) { searchBarState, notes, message ->
            Loaded(
                searchBarState = searchBarState,
                toastMessage = message,
                isLoading = false,
                notes = notes,
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

    fun onToastMessageShown(id: Long) {
        when (state.value) {
            is InitialLoad -> error("Toast message cannot be shown during initial load.")
            is Loaded -> viewModelScope.launch {
                toastMessageManager.clearMessage(id)
            }
        }
    }
}
