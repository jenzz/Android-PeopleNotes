package com.jenzz.peoplenotes.common.data

import javax.inject.Inject

class PeopleRepository @Inject constructor(
    private val localDataSource: PeopleDataSource,
) {

    suspend fun add(person: Person) {
        localDataSource.add(person)
    }
}
