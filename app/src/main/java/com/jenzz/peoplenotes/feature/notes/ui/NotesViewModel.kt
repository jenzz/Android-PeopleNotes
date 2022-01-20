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
import com.jenzz.peoplenotes.feature.notes.data.Notes
import com.jenzz.peoplenotes.feature.notes.data.NotesUseCases
import com.jenzz.peoplenotes.feature.notes.ui.NotesUiState.InitialLoad
import com.jenzz.peoplenotes.feature.notes.ui.NotesUiState.Loaded
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: NotesUseCases,
) : ViewModel() {

    private val personId = NotesScreenDestination.argsFrom(savedStateHandle).personId
    private var currentObserveNotes: Job? = null
    private lateinit var searchBarState: SearchBarState

    var state by mutableStateOf<NotesUiState>(InitialLoad)
        private set

    fun init(searchBarState: SearchBarState) {
        this.searchBarState = searchBarState
        observeNotes { notes ->
            state = Loaded(
                toastMessage = null,
                isLoading = false,
                notes = notes,
            )
        }
    }

    fun onSearchTermChange(searchTerm: String) {
        when (val state = state) {
            is InitialLoad -> {
                /* Keep loading… */
            }
            is Loaded -> {
                this.state = state.copy(isLoading = true)
            }
        }
        observeNotes(filter = searchTerm) { notes ->
            this.state = Loaded(
                toastMessage = null,
                isLoading = false,
                notes = notes,
            )
        }
    }

    fun onSortByChange(sortBy: SortBy) {
        when (val state = state) {
            is InitialLoad -> error("Sort by cannot change during initial load.")
            is Loaded -> {
                this.state = state.copy(isLoading = true)
                observeNotes(sortBy = sortBy) { notes ->
                    this.state = state.copy(
                        isLoading = false,
                        notes = notes,
                        toastMessage = ToastMessage(
                            text = TextResource.fromId(id = R.string.sorted_by, sortBy.label)
                        ),
                    )
                }
            }
        }
    }

    fun onToastMessageShown() {
        when (val state = state) {
            is InitialLoad -> error("Toast message cannot be shown during initial load.")
            is Loaded -> this.state = state.copy(toastMessage = null)
        }
    }

    private inline fun observeNotes(
        sortBy: SortBy = searchBarState.sortBy.selected,
        filter: String = searchBarState.searchTerm,
        crossinline action: suspend (value: Notes) -> Unit,
    ) {
        currentObserveNotes?.cancel()
        currentObserveNotes = viewModelScope.launch {
            useCases
                .observeNotesWithPerson(personId, sortBy, filter)
                .collect(action)
        }
    }
}
