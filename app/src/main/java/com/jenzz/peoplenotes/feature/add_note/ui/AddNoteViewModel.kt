package com.jenzz.peoplenotes.feature.add_note.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.data.notes.NoteId
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.ui.TextFieldUiState
import com.jenzz.peoplenotes.common.ui.TextResource
import com.jenzz.peoplenotes.feature.add_note.data.AddNoteUseCases
import com.jenzz.peoplenotes.feature.add_note.data.SaveNoteResult
import com.jenzz.peoplenotes.feature.add_note.ui.AddNoteUiState.InitialLoad
import com.jenzz.peoplenotes.feature.add_note.ui.AddNoteUiState.Loaded
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    navArgs: AddNoteScreenNavArgs,
    private val useCases: AddNoteUseCases,
) : ViewModel() {

    private val personId = navArgs.personId
    private val noteId = navArgs.noteId

    var state by mutableStateOf<AddNoteUiState>(InitialLoad)
        private set

    init {
        if (noteId != null) {
            initEditNote(noteId)
        } else {
            initNewNote()
        }
    }

    private fun initEditNote(noteId: NoteId) {
        viewModelScope.launch {
            val note = useCases.getNote(noteId)
            state = createLoadedState(note = note.text.value, person = note.person)
        }
    }

    private fun initNewNote() {
        viewModelScope.launch {
            val person = useCases.getPerson(personId)
            state = createLoadedState(note = "", person = person)
        }
    }

    fun onNoteChange(note: String) {
        when (val state = state) {
            is InitialLoad ->
                error("Note cannot change during initial load.")
            is Loaded ->
                this.state = state.copy(note = state.note.copy(value = note, error = null))
        }
    }

    fun onAddNote() {
        when (val state = state) {
            is InitialLoad ->
                error("Note cannot be saved during initial load.")
            is Loaded ->
                viewModelScope.launch {
                    saveNote(state)
                }
        }
    }

    private fun createLoadedState(note: String, person: Person): Loaded =
        Loaded(
            note = TextFieldUiState(
                value = note,
                label = TextResource.fromId(
                    id = R.string.note_about,
                    person.fullName
                )
            ),
            inputsEnabled = true,
            isNoteAdded = false,
        )

    private suspend fun saveNote(state: Loaded) {
        this.state = state.copy(inputsEnabled = false)
        this.state = when (val result = useCases.saveNote(state.note.value, noteId, personId)) {
            is SaveNoteResult.Success ->
                state.copy(isNoteAdded = true)
            is SaveNoteResult.Error ->
                state.copy(
                    inputsEnabled = true,
                    note = state.note.copy(error = result.error)
                )
        }
    }
}
