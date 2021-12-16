package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.common.data.notes.NewNote
import com.jenzz.peoplenotes.common.data.notes.NotesDataSource
import javax.inject.Inject

class PeopleAndNotesRepository @Inject constructor(
    private val peopleLocalDataSource: PeopleDataSource,
    private val notesLocalDataSource: NotesDataSource,
) {

    suspend fun add(newPerson: NewPerson, newNote: NewNote?) {
        val person = peopleLocalDataSource.add(newPerson)
        if (newNote != null) {
            notesLocalDataSource.add(newNote, person.id)
        }
    }
}
