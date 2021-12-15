package com.jenzz.peoplenotes.common.data

import javax.inject.Inject

interface PeopleDataSource {

    suspend fun add(person: Person)
}

class PeopleLocalDataSource @Inject constructor(

) : PeopleDataSource {

    override suspend fun add(person: Person) {
        // TODO JD SQLDELIGHT
    }
}
