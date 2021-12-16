package com.jenzz.peoplenotes.feature.home.data

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.feature.home.ui.HomeUiState
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
            useCases.getPeople().collect { people ->
                _state.value = HomeUiState.Loaded(people)
            }
        }
    }
}
