package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.ext.NonEmptyString

data class Person(
    val id: PersonId,
    val firstName: NonEmptyString,
    val lastName: NonEmptyString,
    val lastModified: String, // TODO JD Date.
)

@JvmInline
value class PersonId(val value: Int)
