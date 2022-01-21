package com.jenzz.peoplenotes.feature.add_note.data

import androidx.annotation.StringRes
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.data.notes.NewNote
import com.jenzz.peoplenotes.common.data.notes.Note
import com.jenzz.peoplenotes.common.data.notes.NoteId
import com.jenzz.peoplenotes.common.data.notes.NotesRepository
import com.jenzz.peoplenotes.common.data.people.PeopleRepository
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.data.people.PersonId
import javax.inject.Inject

data class AddNoteUseCases @Inject constructor(
    val saveNote: SaveNoteUseCase,
    val getNote: GetNoteUseCase,
    val getPerson: GetPersonUseCase,
)

class SaveNoteUseCase @Inject constructor(
    private val noteValidator: AddNoteValidator,
    private val notesRepository: NotesRepository,
) {

    suspend operator fun invoke(
        note: String,
        noteId: NoteId?,
        personId: PersonId,
    ): SaveNoteResult =
        if (noteId != null)
            updateNote(note, noteId)
        else
            addNote(note, personId)

    private suspend fun updateNote(note: String, noteId: NoteId): SaveNoteResult =
        when (val validatedNote = noteValidator.validate(note)) {
            is AddNoteValidationResult.Invalid ->
                SaveNoteResult.Error(R.string.note_cannot_be_empty)
            is AddNoteValidationResult.Valid -> {
                val validNote = NewNote(validatedNote.note)
                notesRepository.update(noteId = noteId, note = validNote)
                SaveNoteResult.Success
            }
        }

    private suspend fun addNote(note: String, personId: PersonId): SaveNoteResult =
        when (val validatedNote = noteValidator.validate(note)) {
            is AddNoteValidationResult.Invalid ->
                SaveNoteResult.Error(R.string.note_cannot_be_empty)
            is AddNoteValidationResult.Valid -> {
                val validNote = NewNote(validatedNote.note)
                notesRepository.add(note = validNote, personId = personId)
                SaveNoteResult.Success
            }
        }
}

class GetNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {

    suspend operator fun invoke(noteId: NoteId): Note =
        notesRepository.get(noteId)
}

class GetPersonUseCase @Inject constructor(
    private val peopleRepository: PeopleRepository,
) {

    suspend operator fun invoke(personId: PersonId): Person =
        peopleRepository.get(personId)
}

sealed class SaveNoteResult {

    object Success : SaveNoteResult()

    data class Error(@StringRes val error: Int) : SaveNoteResult()
}
