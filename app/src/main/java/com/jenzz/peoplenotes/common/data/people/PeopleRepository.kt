package com.jenzz.peoplenotes.common.data.people

import javax.inject.Inject

class PeopleRepository @Inject constructor(
    private val localDataSource: PeopleDataSource,
) {

    suspend fun add(person: NewPerson) {
        localDataSource.add(person)
    }
}
