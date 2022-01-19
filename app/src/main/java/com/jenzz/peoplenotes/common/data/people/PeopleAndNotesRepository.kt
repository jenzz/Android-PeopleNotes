package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.common.data.notes.NewNote
import com.jenzz.peoplenotes.common.data.notes.NotesList
import com.jenzz.peoplenotes.common.data.notes.NotesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PeopleAndNotesRepository @Inject constructor(
    private val peopleRepository: PeopleRepository,
    private val notesRepository: NotesRepository,
) {

    suspend fun add(newPerson: NewPerson, newNote: NewNote?) {
        val person = peopleRepository.add(newPerson)
        if (newNote != null) {
            notesRepository.add(newNote, person.id)
        }
    }

    suspend fun delete(personId: PersonId): DeletePersonResult {
        val notes = notesRepository.observeNotes(personId).first()
        return if (notes.isEmpty) {
            peopleRepository.delete(personId)
            DeletePersonResult.Success
        } else {
            DeletePersonResult.RemainingNotesForPerson(personId, notes)
        }
    }

    suspend fun deleteWithNotes(personId: PersonId) {
        notesRepository.deleteAllByPerson(personId)
        peopleRepository.delete(personId)
    }
}

sealed class DeletePersonResult {

    object Success : DeletePersonResult()

    data class RemainingNotesForPerson(
        val personId: PersonId,
        val notes: NotesList,
    ) : DeletePersonResult()
}
