package com.jenzz.peoplenotes.common.data.notes

import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.feature.home.ui.SortBy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotesRepository @Inject constructor(
    private val localDataSource: NotesDataSource,
) {

    fun getAllNotes(sortBy: SortBy, filter: String): Flow<List<Note>> =
        localDataSource.getAllNotes(sortBy, filter)

    suspend fun add(note: NewNote, personId: PersonId) {
        localDataSource.add(note, personId)
    }

    suspend fun delete(id: NoteId) {
        localDataSource.delete(id)
    }
}
