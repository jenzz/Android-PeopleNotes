package com.jenzz.peoplenotes.common.data.notes

import com.jenzz.peoplenotes.common.data.people.PersonId
import javax.inject.Inject

class NotesRepository @Inject constructor(
    private val localDataSource: NotesDataSource,
) {

    suspend fun add(note: NewNote, personId: PersonId) {
        localDataSource.add(note, personId)
    }

    suspend fun deleteAllNotesByPerson(personId: PersonId) {
        localDataSource.deleteAllNotesByPerson(personId)
    }
}
