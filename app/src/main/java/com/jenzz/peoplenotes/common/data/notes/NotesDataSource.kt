package com.jenzz.peoplenotes.common.data.notes

import com.jenzz.peoplenotes.common.data.NoteQueries
import com.jenzz.peoplenotes.common.data.people.PersonId
import javax.inject.Inject

interface NotesDataSource {

    suspend fun add(note: NewNote, personId: PersonId)

    suspend fun deleteAllNotesByPerson(personId: PersonId)
}

class NotesLocalDataSource @Inject constructor(
    private val noteQueries: NoteQueries,
) : NotesDataSource {

    override suspend fun add(note: NewNote, personId: PersonId) {
        noteQueries.insert(note.text.toString(), personId.value)
    }

    override suspend fun deleteAllNotesByPerson(personId: PersonId) {
        noteQueries.deleteAllNotesByPerson(personId.value)
    }
}
