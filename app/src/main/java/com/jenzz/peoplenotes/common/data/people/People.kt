package com.jenzz.peoplenotes.common.data.people

data class People(
    val persons: List<Person> = emptyList(),
    val totalCount: Int = 0,
) {

    val isEmpty: Boolean = persons.isEmpty()
}
