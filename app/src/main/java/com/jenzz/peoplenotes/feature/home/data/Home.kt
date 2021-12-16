package com.jenzz.peoplenotes.feature.home.data

import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.feature.home.ui.SortBy

data class Home(
    val sortedBy: SortBy,
    val people: List<Person>,
)
