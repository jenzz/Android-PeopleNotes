package com.jenzz.peoplenotes.feature.person_details.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.feature.destinations.PersonDetailsScreenDestination
import com.jenzz.peoplenotes.feature.person_details.data.PersonDetails
import com.jenzz.peoplenotes.feature.person_details.data.PersonDetailsUseCases
import com.jenzz.peoplenotes.feature.person_details.ui.PersonDetailsUiState.Loading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    useCases: PersonDetailsUseCases,
) : ViewModel() {

    var state by mutableStateOf<PersonDetailsUiState>(Loading)
        private set

    init {
        val personId = PersonDetailsScreenDestination.argsFrom(savedStateHandle).personId
        viewModelScope.launch {
            useCases
                .getNotesUseCase(personId)
                .collect { notes ->
                    state = PersonDetailsUiState.Loaded(
                        personDetails = PersonDetails(
                            notes = notes,
                        )
                    )
                }
        }
    }
}