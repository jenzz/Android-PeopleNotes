package com.jenzz.peoplenotes.common.data.notes

import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.ext.NonEmptyString

data class Note(
    val id: NoteId,
    val text: NonEmptyString,
    val lastModified: String, // TODO JD Date.
    val person: Person,
)

@JvmInline
value class NoteId(val value: Int)
