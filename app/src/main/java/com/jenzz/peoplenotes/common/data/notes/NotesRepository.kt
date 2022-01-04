package com.jenzz.peoplenotes.common.data.notes

import com.jenzz.peoplenotes.common.data.people.PersonId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotesRepository @Inject constructor(
    private val localDataSource: NotesDataSource,
) {

    fun getNotes(personId: PersonId): Flow<List<Note>> =
        localDataSource.getNotes(personId)

    suspend fun add(note: NewNote, personId: PersonId) {
        localDataSource.add(note, personId)
    }

    suspend fun delete(id: NoteId) {
        localDataSource.delete(id)
    }

    suspend fun deleteAllByPerson(personId: PersonId) {
        localDataSource.deleteAllByPerson(personId)
    }
}
