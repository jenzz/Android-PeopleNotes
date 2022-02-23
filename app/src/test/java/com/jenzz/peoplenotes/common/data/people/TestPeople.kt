package com.jenzz.peoplenotes.common.data.people

import androidx.compose.ui.graphics.Color
import com.jenzz.peoplenotes.ext.random
import com.jenzz.peoplenotes.ext.toNonEmptyString
import java.time.LocalDateTime

val people = People(
    persons = listOf(
        Person(
            id = PersonId(1),
            firstName = FirstName("first name".toNonEmptyString()),
            lastName = LastName("last name".toNonEmptyString()),
            color = Color.random(),
            lastModified = LocalDateTime.now(),
        ),
    ),
    totalCount = 1,
)
