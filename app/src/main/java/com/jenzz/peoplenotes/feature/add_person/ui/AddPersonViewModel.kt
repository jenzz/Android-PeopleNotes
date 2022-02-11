package com.jenzz.peoplenotes.feature.add_person.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.ui.TextFieldUiState
import com.jenzz.peoplenotes.common.ui.TextResource
import com.jenzz.peoplenotes.ext.saveableStateFlowOf
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

    val initialState = AddPersonUiState(
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

    private val _state = savedStateHandle.saveableStateFlowOf(
        key = "add_person",
        initialValue = initialState,
    )
    val state = _state.asStateFlow()

    fun onFirstNameChange(name: String) {
        _state.value = state.value.copy(
            firstName = state.value.firstName.copy(value = name, error = null)
        )
    }

    fun onLastNameChange(name: String) {
        _state.value = state.value.copy(
            lastName = state.value.lastName.copy(value = name, error = null)
        )
    }

    fun onNoteChange(note: String) {
        _state.value = state.value.copy(
            note = state.value.note.copy(value = note, error = null)
        )
    }

    fun onAddPerson() {
        _state.value = state.value.copy(inputsEnabled = false)
        viewModelScope.launch {
            addPersonWithNote()
        }
    }

    private suspend fun addPersonWithNote() {
        val result = useCases.addPersonWithNote(
            firstName = state.value.firstName.value,
            lastName = state.value.lastName.value,
            note = state.value.note.value,
        )
        _state.value = when (result) {
            is AddPersonResult.Success ->
                state.value.copy(isUserAdded = true)
            is AddPersonResult.Error ->
                state.value.copy(
                    inputsEnabled = true,
                    firstName = state.value.firstName.copy(error = result.firstNameError),
                    lastName = state.value.lastName.copy(error = result.lastNameError),
                )
        }
    }
}
