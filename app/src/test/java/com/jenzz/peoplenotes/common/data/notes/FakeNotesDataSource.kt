package com.jenzz.peoplenotes.common.data.notes

import com.jakewharton.rxrelay3.PublishRelay
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.feature.notes.ui.NotesSortBy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

class FakeNotesDataSource : NotesDataSource {

    private val notes = PublishRelay.create<NotesList>()

    override fun observeNotes(personId: PersonId): Observable<NotesList> =
        notes

    override fun observeNotes(
        personId: PersonId,
        sortBy: NotesSortBy,
        filter: String,
    ): Flow<NotesList> {
        TODO("Not yet implemented")
    }

    override suspend fun get(noteId: NoteId): Note {
        TODO("Not yet implemented")
    }

    override suspend fun add(note: NewNote, personId: PersonId) {
        TODO("Not yet implemented")
    }

    override suspend fun update(noteId: NoteId, note: NewNote) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: NoteId) {
        TODO("Not yet implemented")
    }

    override fun deleteAllByPerson(personId: PersonId): Completable {
        TODO("Not yet implemented")
    }

    fun emit(notes: NotesList) {
        this.notes.accept(notes)
    }
}
