package com.jenzz.peoplenotes.feature.notes.data

import com.jenzz.peoplenotes.common.data.notes.NotesList
import com.jenzz.peoplenotes.common.data.people.Person

data class Notes(
    val person: Person? = null, // TODO JD Null while loading...
    val list: NotesList = NotesList(),
) {

    init {
        check(list.items.all { note -> note.person == person }) {
            "Notes must have same author."
        }
    }

    val totalCount: Int = list.totalCount

    val isEmpty: Boolean = list.isEmpty

    fun requirePerson(): Person =
        requireNotNull(person)
}
