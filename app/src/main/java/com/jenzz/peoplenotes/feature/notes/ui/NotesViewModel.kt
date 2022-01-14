package com.jenzz.peoplenotes.feature.notes.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.ListStyle
import com.jenzz.peoplenotes.common.ui.TextResource
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarState
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarUiState
import com.jenzz.peoplenotes.feature.destinations.NotesScreenDestination
import com.jenzz.peoplenotes.feature.notes.data.NotesUseCases
import com.jenzz.peoplenotes.feature.notes.ui.NotesUiState.InitialLoad
import com.jenzz.peoplenotes.feature.notes.ui.NotesUiState.Loaded
import com.jenzz.peoplenotes.feature.people.ui.PeopleSortBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: NotesUseCases,
    private val searchBarState: SearchBarState,
) : ViewModel() {

    private val personId = NotesScreenDestination.argsFrom(savedStateHandle).personId

    var state by mutableStateOf<NotesUiState>(
        InitialLoad(
            searchBarState = SearchBarUiState.DEFAULT,
        )
    )
        private set

    init {
        viewModelScope.launch {
            useCases
                .getNotesWithPerson(personId)
                .collect { notes ->
                    state = Loaded(
                        isLoading = false,
                        searchBarState = state.searchBarState,
                        notes = notes,
                        toastMessage = null,
                    )
                }
        }
    }

    fun onSearchTermChange(searchTerm: String) {
        val loadedState = state as Loaded
        state = loadedState.copy(searchBarState = searchBarState.onSearchTermChange(searchTerm))
        viewModelScope.launch {
            getNotes(filter = searchTerm)
        }
    }

    fun onListStyleChange(listStyle: ListStyle) {
        val loadedState = state as Loaded
        state = loadedState.copy(searchBarState = searchBarState.onListStyleChange(listStyle))
    }

    fun onSortByChange(sortBy: PeopleSortBy) {
        val loadedState = state as Loaded
        state = loadedState.copy(
            searchBarState = searchBarState.onSortByChange(sortBy),
            toastMessage = ToastMessage(
                text = TextResource.fromId(R.string.sorted_by, sortBy.label)
            ),
        )
        viewModelScope.launch {
            getNotes(sortBy = sortBy)
        }
    }

    fun onToastMessageShown() {
        val loadedState = state as Loaded
        state = loadedState.copy(toastMessage = null)
    }

    private suspend fun getNotes(
        sortBy: PeopleSortBy = state.searchBarState.sortBy,
        filter: String = state.searchBarState.searchTerm,
    ) {
        val loadedState = state as Loaded
        state = loadedState.copy(
            isLoading = true
        )
        useCases
            .getNotesWithPerson(personId, sortBy, filter)
            .collect { notes ->
                state = loadedState.copy(
                    isLoading = false,
                    notes = notes,
                )
            }
    }
}