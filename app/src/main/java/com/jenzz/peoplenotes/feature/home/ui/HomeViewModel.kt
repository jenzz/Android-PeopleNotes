package com.jenzz.peoplenotes.feature.home.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.common.data.people.Person
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
            people = emptyList(),
        )
    )
        private set

    init {
        viewModelScope.launch {
            useCases
                .getPeople(
                    sortBy = state.sortBy,
                    filter = state.filter
                )
                .collect { people ->
                    state = state.copy(
                        isLoading = false,
                        people = people
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
            getPeople(sortBy = sortBy, filter = filter)
        }
    }

    fun onSortByChanged(sortBy: SortBy) {
        state = state.copy(sortBy = sortBy)
        viewModelScope.launch {
            getPeople(sortBy = sortBy, filter = "")
        }
    }

    fun onDelete(person: Person) {
        viewModelScope.launch {
            useCases.deletePerson(person.id)
        }
    }

    private suspend fun getPeople(sortBy: SortBy, filter: String) {
        state = state.copy(isLoading = true)
        useCases
            .getPeople(sortBy, filter)
            .collect { people ->
                state = state.copy(
                    isLoading = false,
                    people = people
                )
            }
    }
}
