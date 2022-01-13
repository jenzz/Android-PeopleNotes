package com.jenzz.peoplenotes.feature.person_details.data

import com.jenzz.peoplenotes.common.data.notes.Note
import com.jenzz.peoplenotes.common.data.people.Person

data class PersonDetails(
    val person: Person,
    val notes: List<Note>,
) {

    init {
        check(notes.all { note -> note.person == person }) {
            "Notes must have same author."
        }
    }

    val isEmpty: Boolean =
        notes.isEmpty()
}
