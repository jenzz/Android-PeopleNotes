package com.jenzz.peoplenotes.feature.add_person.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.TextFieldUiState
import com.jenzz.peoplenotes.common.ui.TextResource
import com.jenzz.peoplenotes.ext.mutableStateOf
import com.jenzz.peoplenotes.feature.add_person.data.AddPersonResult
import com.jenzz.peoplenotes.feature.add_person.data.AddPersonUseCases
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
            firstName = TextFieldUiState(
                value = "",
                label = TextResource.fromId(id = R.string.first_name),
            ),
            lastName = TextFieldUiState(
                value = "",
                label = TextResource.fromId(id = R.string.last_name),
            ),
            note = TextFieldUiState(
                value = "",
                label = TextResource.fromId(id = R.string.notes_optional),
            ),
            inputsEnabled = true,
            isUserAdded = false,
        )
    )
        private set

    fun onFirstNameChange(name: String) {
        state = state.copy(firstName = state.firstName.copy(value = name, error = null))
    }

    fun onLastNameChange(name: String) {
        state = state.copy(lastName = state.lastName.copy(value = name, error = null))
    }

    fun onNoteChange(note: String) {
        state = state.copy(note = state.note.copy(value = note, error = null))
    }

    fun onAddPerson() {
        state = state.copy(inputsEnabled = false)
        viewModelScope.launch {
            addPersonWithNote()
        }
    }

    private suspend fun addPersonWithNote() {
        val result = useCases.addPersonWithNote(
            firstName = state.firstName.value,
            lastName = state.lastName.value,
            note = state.note.value,
        )
        state = when (result) {
            is AddPersonResult.Success ->
                state.copy(isUserAdded = true)
            is AddPersonResult.Error ->
                state.copy(
                    inputsEnabled = true,
                    firstName = state.firstName.copy(error = result.firstNameError),
                    lastName = state.lastName.copy(error = result.lastNameError),
                )
        }
    }
}
