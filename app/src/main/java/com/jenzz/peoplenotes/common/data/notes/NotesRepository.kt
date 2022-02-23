package com.jenzz.peoplenotes.common.data.notes

import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.feature.notes.ui.NotesSortBy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotesRepository @Inject constructor(
    private val localDataSource: NotesDataSource,
) {

    fun observeNotes(personId: PersonId): Observable<NotesList> =
        localDataSource.observeNotes(personId)

    fun observeNotes(personId: PersonId, sortBy: NotesSortBy, filter: String): Flow<NotesList> =
        localDataSource.observeNotes(personId, sortBy, filter)

    suspend fun get(noteId: NoteId): Note =
        localDataSource.get(noteId)

    suspend fun add(note: NewNote, personId: PersonId) {
        localDataSource.add(note, personId)
    }

    suspend fun update(noteId: NoteId, note: NewNote) {
        localDataSource.update(noteId, note)
    }

    suspend fun delete(id: NoteId) {
        localDataSource.delete(id)
    }

    fun deleteAllByPerson(personId: PersonId): Completable =
        localDataSource.deleteAllByPerson(personId)
}
