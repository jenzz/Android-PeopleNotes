package com.jenzz.peoplenotes.common.data.notes

import androidx.compose.ui.graphics.Color
import com.jenzz.peoplenotes.common.data.CoroutineDispatchers
import com.jenzz.peoplenotes.common.data.NoteQueries
import com.jenzz.peoplenotes.common.data.people.FirstName
import com.jenzz.peoplenotes.common.data.people.LastName
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.data.time.Clock
import com.jenzz.peoplenotes.ext.toEntity
import com.jenzz.peoplenotes.ext.toLocalDateTime
import com.jenzz.peoplenotes.ext.toNonEmptyString
import com.jenzz.peoplenotes.feature.notes.ui.NotesSortBy
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface NotesDataSource {

    fun observeNotes(personId: PersonId): Flow<NotesList>

    fun observeNotes(personId: PersonId, sortBy: NotesSortBy, filter: String): Flow<NotesList>

    suspend fun get(noteId: NoteId): Note

    suspend fun add(note: NewNote, personId: PersonId)

    suspend fun update(noteId: NoteId, note: NewNote)

    suspend fun delete(id: NoteId)

    suspend fun deleteAllByPerson(personId: PersonId)
}

class NotesLocalDataSource @Inject constructor(
    private val noteQueries: NoteQueries,
    private val dispatchers: CoroutineDispatchers,
    private val clock: Clock,
) : NotesDataSource {

    private val toNote = {
            id: Int,
            text: String,
            personId: Int,
            noteLastModified: String,
            _: Int,
            firstName: String,
            lastName: String,
            color: Int,
            personLastModified: String,
        ->
        Note(
            id = NoteId(id),
            text = text.toNonEmptyString(),
            lastModified = noteLastModified.toLocalDateTime(),
            person = Person(
                id = PersonId(personId),
                firstName = FirstName(firstName.toNonEmptyString()),
                lastName = LastName(lastName.toNonEmptyString()),
                color = Color(color),
                lastModified = personLastModified.toLocalDateTime(),
            ),
        )
    }

    override fun observeNotes(personId: PersonId): Flow<NotesList> =
        noteQueries
            .selectAll(personId.value, toNote)
            .asFlow()
            .mapToList(dispatchers.Default)
            .map { notes ->
                withContext(dispatchers.Default) {
                    NotesList(
                        items = notes,
                        totalCount = noteQueries.count().executeAsOne().toInt(),
                    )
                }
            }

    override fun observeNotes(
        personId: PersonId,
        sortBy: NotesSortBy,
        filter: String,
    ): Flow<NotesList> {
        val comparator = when (sortBy) {
            NotesSortBy.MostRecentFirst -> compareByDescending { note: Note -> note.lastModified }
            NotesSortBy.OldestFirst -> compareBy { note: Note -> note.lastModified }
        }
        val filterSql = if (filter.isNotEmpty()) "%$filter%" else null
        return noteQueries
            .selectAllFilteredByText(personId.value, filterSql, toNote)
            .asFlow()
            .mapToList(dispatchers.Default)
            .map { notes ->
                withContext(dispatchers.Default) {
                    NotesList(
                        items = notes.sortedWith(comparator),
                        totalCount = noteQueries.count().executeAsOne().toInt(),
                    )
                }
            }
    }

    override suspend fun get(noteId: NoteId): Note =
        withContext(dispatchers.Default) {
            noteQueries
                .selectById(noteId.value, toNote)
                .executeAsOne()
        }

    override suspend fun add(note: NewNote, personId: PersonId) {
        withContext(dispatchers.Default) {
            noteQueries.insert(
                text = note.text.toString(),
                personId = personId.value,
                lastModified = clock.now().toEntity(),
            )
        }
    }

    override suspend fun update(noteId: NoteId, note: NewNote) {
        withContext(dispatchers.Default) {
            noteQueries.update(
                noteId = noteId.value,
                text = note.text.value,
                lastModified = clock.now().toEntity(),
            )
        }
    }

    override suspend fun delete(id: NoteId) {
        withContext(dispatchers.Default) {
            noteQueries.delete(id.value)
        }
    }

    override suspend fun deleteAllByPerson(personId: PersonId) {
        withContext(dispatchers.Default) {
            noteQueries.deleteAllByPerson(personId.value)
        }
    }
}
