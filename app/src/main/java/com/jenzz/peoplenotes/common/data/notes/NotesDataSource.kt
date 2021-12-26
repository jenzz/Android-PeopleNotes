package com.jenzz.peoplenotes.common.data.notes

import com.jenzz.peoplenotes.common.data.NoteQueries
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.data.people.di.FirstName
import com.jenzz.peoplenotes.common.data.people.di.LastName
import com.jenzz.peoplenotes.ext.toNonEmptyString
import com.jenzz.peoplenotes.feature.home.ui.SortBy
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface NotesDataSource {

    fun getAllNotes(sortBy: SortBy, filter: String): Flow<List<Note>>

    suspend fun add(note: NewNote, personId: PersonId)

    suspend fun delete(id: NoteId)
}

class NotesLocalDataSource @Inject constructor(
    private val noteQueries: NoteQueries,
) : NotesDataSource {

    private val toNote = {
            id: Int,
            text: String,
            personId: Int,
            lastModified: String,
            _: Int,
            firstName: String,
            lastName: String,
        ->
        Note(
            id = NoteId(id),
            text = text.toNonEmptyString(),
            lastModified = lastModified,
            person = Person(
                id = PersonId(personId),
                firstName = FirstName(firstName.toNonEmptyString()),
                lastName = LastName(lastName.toNonEmptyString()),
            ),
        )
    }

    override fun getAllNotes(sortBy: SortBy, filter: String): Flow<List<Note>> {
        val orderBy = when (sortBy) {
            SortBy.FirstName -> compareBy { note: Note -> note.person.firstName }
            SortBy.LastName -> compareBy { note: Note -> note.person.lastName }
            SortBy.LastModified -> compareByDescending { note: Note -> note.lastModified }
        }
        val filterSql = if (filter.isNotEmpty()) "%$filter%" else null
        return noteQueries
            .selectAllWithPeople(filterSql, toNote)
            .asFlow()
            .mapToList()
            .map { notes -> notes.sortedWith(orderBy) }
    }

    override suspend fun add(note: NewNote, personId: PersonId) {
        noteQueries.insert(note.text.toString(), personId.value)
    }

    override suspend fun delete(id: NoteId) {
        noteQueries.delete(id.value)
    }
}
