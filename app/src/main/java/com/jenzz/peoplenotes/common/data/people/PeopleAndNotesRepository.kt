package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.common.data.notes.NewNote
import com.jenzz.peoplenotes.common.data.notes.NotesRepository
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
}
