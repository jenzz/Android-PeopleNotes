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
            val person = peopleRepository.delete(personId)
            DeletePersonResult.Success(person)
        } else {
            val person = peopleRepository.get(personId)
            DeletePersonResult.RemainingNotesForPerson(person, notes)
        }
    }

    suspend fun deleteWithNotes(personId: PersonId): Person {
        notesRepository.deleteAllByPerson(personId)
        return peopleRepository.delete(personId)
    }
}

sealed class DeletePersonResult {

    data class Success(
        val person: Person,
    ) : DeletePersonResult()

    data class RemainingNotesForPerson(
        val person: Person,
        val notes: NotesList,
    ) : DeletePersonResult()
}
