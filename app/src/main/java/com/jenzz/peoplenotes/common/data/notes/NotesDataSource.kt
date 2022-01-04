package com.jenzz.peoplenotes.common.data.notes

import com.jenzz.peoplenotes.common.data.NoteQueries
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.data.people.di.FirstName
import com.jenzz.peoplenotes.common.data.people.di.LastName
import com.jenzz.peoplenotes.ext.toNonEmptyString
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface NotesDataSource {

    fun getNotes(personId: PersonId): Flow<List<Note>>

    suspend fun add(note: NewNote, personId: PersonId)

    suspend fun delete(id: NoteId)

    suspend fun deleteAllByPerson(personId: PersonId)
}

class NotesLocalDataSource @Inject constructor(
    private val noteQueries: NoteQueries,
) : NotesDataSource {

    private val toNote = {
            id: Int,
            text: String,
            personId: Int,
            noteLastModified: String,
            _: Int,
            firstName: String,
            lastName: String,
            personLastModified: String,
        ->
        Note(
            id = NoteId(id),
            text = text.toNonEmptyString(),
            lastModified = noteLastModified,
            person = Person(
                id = PersonId(personId),
                firstName = FirstName(firstName.toNonEmptyString()),
                lastName = LastName(lastName.toNonEmptyString()),
                lastModified = personLastModified,
            ),
        )
    }

    override fun getNotes(personId: PersonId): Flow<List<Note>> =
        noteQueries.selectAll(personId.value, toNote).asFlow().mapToList()

    override suspend fun add(note: NewNote, personId: PersonId) {
        noteQueries.insert(note.text.toString(), personId.value)
    }

    override suspend fun delete(id: NoteId) {
        noteQueries.delete(id.value)
    }

    override suspend fun deleteAllByPerson(personId: PersonId) {
        noteQueries.deleteAllByPerson(personId.value)
    }
}
