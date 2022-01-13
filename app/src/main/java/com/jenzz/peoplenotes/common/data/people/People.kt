package com.jenzz.peoplenotes.common.data.people

data class People(
    val persons: List<Person>,
    val totalCount: Int,
) {

    companion object {

        val DEFAULT = People(
            persons = emptyList(),
            totalCount = 0,
        )
    }

    val isEmpty: Boolean = persons.isEmpty()
}