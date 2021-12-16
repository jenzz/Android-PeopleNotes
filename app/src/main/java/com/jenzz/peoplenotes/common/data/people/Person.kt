package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.common.data.people.di.FirstName
import com.jenzz.peoplenotes.common.data.people.di.LastName

data class Person(
    val id: PersonId,
    val firstName: FirstName,
    val lastName: LastName,
    val lastModified: String, // TODO JD Date.
)

@JvmInline
value class PersonId(val value: Int)
