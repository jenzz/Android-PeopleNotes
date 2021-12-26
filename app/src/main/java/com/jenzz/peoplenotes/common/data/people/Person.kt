package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.common.data.people.di.FirstName
import com.jenzz.peoplenotes.common.data.people.di.LastName

data class Person(
    val id: PersonId,
    val firstName: FirstName,
    val lastName: LastName,
) {

    val fullName: String = "${firstName.value} ${lastName.value}"

    val firstNameLetter: Char = firstName.toString().first()
}

@JvmInline
value class PersonId(val value: Int)
