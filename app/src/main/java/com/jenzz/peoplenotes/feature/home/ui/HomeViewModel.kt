package com.jenzz.peoplenotes.feature.home.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.common.data.notes.Note
import com.jenzz.peoplenotes.ext.mutableStateOf
import com.jenzz.peoplenotes.feature.home.data.HomeUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: HomeUseCases,
) : ViewModel() {

    var state by savedStateHandle.mutableStateOf(
        defaultValue = HomeUiState(
            isLoading = true,
            filter = "",
            listStyle = ListStyle.DEFAULT,
            sortBy = SortBy.DEFAULT,
            notes = emptyList(),
        )
    )
        private set

    init {
        viewModelScope.launch {
            useCases
                .getNotes(
                    sortBy = state.sortBy,
                    filter = state.filter
                )
                .collect { notes ->
                    state = state.copy(
                        isLoading = false,
                        notes = notes
                    )
                }
        }
    }

    fun onListStyleChanged(listStyle: ListStyle) {
        state = state.copy(listStyle = listStyle)
    }

    fun onFilterChanged(filter: String) {
        state = state.copy(filter = filter)
        viewModelScope.launch {
            val sortBy = checkNotNull(state.sortBy) { "Missing required sort order." }
            getNotes(sortBy = sortBy, filter = filter)
        }
    }

    fun onSortByChanged(sortBy: SortBy) {
        state = state.copy(sortBy = sortBy)
        viewModelScope.launch {
            getNotes(sortBy = sortBy, filter = "")
        }
    }

    fun onDelete(note: Note) {
        viewModelScope.launch {
            useCases.deleteNote(note.id)
        }
    }

    private suspend fun getNotes(sortBy: SortBy, filter: String) {
        state = state.copy(isLoading = true)
        useCases
            .getNotes(sortBy, filter)
            .collect { notes ->
                state = state.copy(
                    isLoading = false,
                    notes = notes
                )
            }
    }
}
