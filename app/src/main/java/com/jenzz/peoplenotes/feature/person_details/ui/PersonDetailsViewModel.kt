package com.jenzz.peoplenotes.feature.person_details.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.TextResource
import com.jenzz.peoplenotes.common.ui.ToastMessage
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarState
import com.jenzz.peoplenotes.common.ui.widgets.SearchBarUiState
import com.jenzz.peoplenotes.feature.destinations.PersonDetailsScreenDestination
import com.jenzz.peoplenotes.feature.home.ui.ListStyle
import com.jenzz.peoplenotes.feature.home.ui.SortBy
import com.jenzz.peoplenotes.feature.person_details.data.PersonDetailsUseCases
import com.jenzz.peoplenotes.feature.person_details.ui.PersonDetailsUiState.InitialLoad
import com.jenzz.peoplenotes.feature.person_details.ui.PersonDetailsUiState.Loaded
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: PersonDetailsUseCases,
    private val searchBarState: SearchBarState,
) : ViewModel() {

    private val personId = PersonDetailsScreenDestination.argsFrom(savedStateHandle).personId

    var state by mutableStateOf<PersonDetailsUiState>(
        InitialLoad(
            searchBarState = SearchBarUiState.DEFAULT,
        )
    )
        private set

    init {
        viewModelScope.launch {
            useCases
                .getPersonDetails(personId)
                .collect { personDetails ->
                    state = Loaded(
                        isLoading = false,
                        searchBarState = state.searchBarState,
                        personDetails = personDetails,
                        toastMessage = null,
                    )
                }
        }
    }

    fun onSearchTermChange(searchTerm: String) {
        val loadedState = state as Loaded
        state = loadedState.copy(searchBarState = searchBarState.onSearchTermChange(searchTerm))
        viewModelScope.launch {
            getPersonDetails(filter = searchTerm)
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
                text = TextResource.fromId(R.string.sorted_by, sortBy.label)
            ),
        )
        viewModelScope.launch {
            getPersonDetails(sortBy = sortBy)
        }
    }

    fun onToastMessageShown() {
        val loadedState = state as Loaded
        state = loadedState.copy(toastMessage = null)
    }

    private suspend fun getPersonDetails(
        sortBy: SortBy = state.searchBarState.sortBy,
        filter: String = state.searchBarState.searchTerm,
    ) {
        val loadedState = state as Loaded
        state = loadedState.copy(
            isLoading = true
        )
        useCases
            .getPersonDetails(personId, sortBy, filter)
            .collect { personDetails ->
                state = loadedState.copy(
                    isLoading = false,
                    personDetails = personDetails,
                )
            }
    }
}