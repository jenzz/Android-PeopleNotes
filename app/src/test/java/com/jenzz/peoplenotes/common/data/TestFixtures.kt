package com.jenzz.peoplenotes.common.data

import androidx.compose.ui.graphics.Color
import com.jenzz.peoplenotes.common.data.people.FirstName
import com.jenzz.peoplenotes.common.data.people.LastName
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.ext.random
import com.jenzz.peoplenotes.ext.toNonEmptyString
import java.time.LocalDateTime

val JohnDoe = Person(
    id = PersonId(1),
    firstName = FirstName("John".toNonEmptyString()),
    lastName = LastName("Doe".toNonEmptyString()),
    color = Color.random(),
    lastModified = LocalDateTime.now(),
)
