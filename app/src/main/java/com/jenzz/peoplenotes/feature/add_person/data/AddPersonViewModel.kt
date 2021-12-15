package com.jenzz.peoplenotes.feature.add_person.data

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.common.data.Person
import com.jenzz.peoplenotes.common.data.mutableStateOf
import com.jenzz.peoplenotes.feature.add_person.ui.AddPersonUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPersonViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: AddPersonUseCases,
) : ViewModel() {

    var state by savedStateHandle.mutableStateOf(
        AddPersonUiState(
            firstName = "",
            lastName = "",
            notes = "",
            inputsEnabled = true,
            isUserAdded = false,
        )
    )
        private set

    fun onFirstNameChanged(name: String) {
        state = state.copy(firstName = name)
    }

    fun onLastNameChanged(name: String) {
        state = state.copy(lastName = name)
    }

    fun onNotesChanged(notes: String) {
        state = state.copy(notes = notes)
    }

    fun onAddPerson() {
        state = state.copy(inputsEnabled = false)
        viewModelScope.launch {
            useCases.addPerson(Person(state.firstName, state.lastName))
        }
        state = state.copy(isUserAdded = true)
    }
}
