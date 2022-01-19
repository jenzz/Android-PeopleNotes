package com.jenzz.peoplenotes.feature.notes.data

import com.jenzz.peoplenotes.common.data.notes.NotesList
import com.jenzz.peoplenotes.common.data.people.Person

data class Notes(
    val person: Person,
    val notes: NotesList,
) {

    init {
        check(notes.items.all { note -> note.person == person }) {
            "Notes must have same author."
        }
    }

    val isEmpty: Boolean =
        notes.isEmpty
}
