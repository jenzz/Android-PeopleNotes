package com.jenzz.peoplenotes.common.data.notes

import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.ext.NonEmptyString
import java.time.LocalDateTime

data class Note(
    val id: NoteId,
    val text: NonEmptyString,
    val lastModified: LocalDateTime,
    val person: Person,
)

@JvmInline
value class NoteId(val value: Int)
