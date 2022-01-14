package com.jenzz.peoplenotes.feature.notes.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.*
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarState
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarUiState
import com.jenzz.peoplenotes.feature.destinations.NotesScreenDestination
import com.jenzz.peoplenotes.feature.notes.data.NotesUseCases
import com.jenzz.peoplenotes.feature.notes.ui.NotesUiState.InitialLoad
import com.jenzz.peoplenotes.feature.notes.ui.NotesUiState.Loaded
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: NotesUseCases,
) : ViewModel() {

    private val personId = NotesScreenDestination.argsFrom(savedStateHandle).personId
    private val searchBarState: SearchBarState = SearchBarState(
        initialState = SearchBarUiState(
            searchTerm = "",
            listStyle = ListStyle.DEFAULT,
            sortByState = SortByUiState(
                items = NotesSortBy.toSortBy()
            )
        )
    )

    var state by mutableStateOf<NotesUiState>(
        InitialLoad(searchBarState = searchBarState.state)
    )
        private set

    init {
        viewModelScope.launch {
            useCases
                .getNotesWithPerson(
                    personId = personId,
                    sortBy = state.searchBarState.sortByState.selected,
                    filter = state.searchBarState.searchTerm,
                )
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

    fun onSortByChange(sortBy: SortBy) {
        val loadedState = state as Loaded
        state = loadedState.copy(
            searchBarState = searchBarState.onSortByChange(sortBy),
            toastMessage = ToastMessage(
                text = TextResource.fromId(id = R.string.sorted_by, sortBy.label)
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
        sortBy: SortBy = state.searchBarState.sortByState.selected,
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
