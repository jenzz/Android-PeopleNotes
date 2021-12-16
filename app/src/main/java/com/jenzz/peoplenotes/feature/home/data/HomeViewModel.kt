package com.jenzz.peoplenotes.feature.home.data

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private suspend fun getPeople(sortBy: SortBy) {
        useCases
            .getPeople(sortBy)
            .collect { home ->
                _state.value = HomeUiState.Loaded(home)
            }
    }
}
