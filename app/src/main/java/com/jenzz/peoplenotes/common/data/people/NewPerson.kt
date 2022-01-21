package com.jenzz.peoplenotes.common.data.people

import androidx.compose.ui.graphics.Color
import com.jenzz.peoplenotes.common.data.people.di.FirstName
import com.jenzz.peoplenotes.common.data.people.di.LastName

data class NewPerson(
    val firstName: FirstName,
    val lastName: LastName,
    val color: Color,
)
