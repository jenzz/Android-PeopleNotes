package com.jenzz.peoplenotes.feature.notes.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.common.ui.ToastMessageId
import com.jenzz.peoplenotes.common.ui.ToastMessageManager
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarState
import com.jenzz.peoplenotes.ext.saveableStateFlowOf
import com.jenzz.peoplenotes.feature.notes.data.NotesUseCases
import com.jenzz.peoplenotes.feature.notes.ui.NotesUiState.InitialLoad
import com.jenzz.peoplenotes.feature.notes.ui.NotesUiState.Loaded
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

    val initialState = InitialLoad

    private val toastMessageManager = ToastMessageManager()
    private val loading = MutableStateFlow(true)
    private val searchBarState = savedStateHandle.saveableStateFlowOf(
        key = "searchBar",
        initialValue = initialState.searchBarState
    )
    private val notes = searchBarState.asStateFlow().flatMapLatest { state ->
        useCases.observeNotesWithPerson(
            personId = navArgs.personId,
            sortBy = state.sortBy.selected,
            filter = state.searchTerm,
        )
    }

    val state = combine(
        searchBarState.asStateFlow(),
        toastMessageManager.message,
        loading,
        notes,
        ::Loaded,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = initialState,
    )

    fun onSearchBarStateChange(state: SearchBarState) {
        searchBarState.value = state
    }

    fun onToastShown(id: ToastMessageId) {
        viewModelScope.launch {
            toastMessageManager.clearMessage(id)
        }
    }
}
