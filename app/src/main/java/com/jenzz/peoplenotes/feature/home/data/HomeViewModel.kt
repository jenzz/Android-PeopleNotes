package com.jenzz.peoplenotes.feature.home.data

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.feature.home.ui.HomeUiState
import com.jenzz.peoplenotes.feature.home.ui.SortBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCases: HomeUseCases,
) : ViewModel() {

    private val _state = mutableStateOf<HomeUiState>(HomeUiState.Loading)
    val state: State<HomeUiState> = _state

    init {
        viewModelScope.launch {
            getPeople(SortBy.LastModified)
        }
    }

    fun onSortBy(sortBy: SortBy) {
        viewModelScope.launch {
            getPeople(sortBy)
        }
    }

    fun onDeletePerson(person: Person) {
        viewModelScope.launch {
            useCases.deletePerson(person.id)
        }
    }

    private suspend fun getPeople(sortBy: SortBy) {
        useCases
            .getPeople(sortBy)
            .collect { home ->
                if (home.people.isEmpty())
                    _state.value = HomeUiState.Empty
                else
                    _state.value = HomeUiState.Loaded(home)
            }
    }
}
