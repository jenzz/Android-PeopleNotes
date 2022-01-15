package com.jenzz.peoplenotes.feature.notes.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.SortBy
import com.jenzz.peoplenotes.common.ui.TextResource
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarState
import com.jenzz.peoplenotes.feature.destinations.NotesScreenDestination
import com.jenzz.peoplenotes.feature.notes.data.NotesUseCases
import com.jenzz.peoplenotes.feature.notes.ui.NotesUiState.InitialLoad
import com.jenzz.peoplenotes.feature.notes.ui.NotesUiState.Loaded
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: NotesUseCases,
) : ViewModel() {

    private val personId = NotesScreenDestination.argsFrom(savedStateHandle).personId

    var state by mutableStateOf<NotesUiState>(InitialLoad)
        private set

    private lateinit var searchBarState: SearchBarState

    fun init(searchBarState: SearchBarState) {
        this.searchBarState = searchBarState
        viewModelScope.launch {
            useCases
                .observeNotesWithPerson(
                    personId = personId,
                    sortBy = searchBarState.sortBy.selected,
                    filter = searchBarState.searchTerm,
                )
                .collect { notes ->
                    state = Loaded(
                        toastMessage = null,
                        isLoading = false,
                        notes = notes,
                    )
                }

        }
    }

    fun onSearchTermChange(searchTerm: String) {
        viewModelScope.launch {
            observeNotes(filter = searchTerm)
        }
    }

    fun onSortByChange(sortBy: SortBy) {
        viewModelScope.launch {
            observeNotes(sortBy = sortBy)
            state = (state as Loaded).copy(
                toastMessage = ToastMessage(
                    text = TextResource.fromId(id = R.string.sorted_by, sortBy.label)
                ),
            )
        }
    }

    fun onToastMessageShown() {
        state = (state as Loaded).copy(toastMessage = null)
    }

    private suspend fun observeNotes(
        sortBy: SortBy = searchBarState.sortBy.selected,
        filter: String = searchBarState.searchTerm,
    ) {
        val state = this.state as Loaded
        this.state = state.copy(isLoading = true)
        useCases
            .observeNotesWithPerson(personId, sortBy, filter)
            .onEach { delay(3000) }
            .collect { notes ->
                this.state = state.copy(
                    isLoading = false,
                    notes = notes,
                )
            }
    }
}
