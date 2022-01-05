package com.jenzz.peoplenotes.common.data.people

data class People(
    val persons: List<Person>,
    val totalCount: Int,
) {

    val isEmpty: Boolean = persons.isEmpty()
}