package com.jenzz.peoplenotes.common.data.people

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PeopleRepository @Inject constructor(
    private val localDataSource: PeopleDataSource,
) {

    val people: Flow<List<Person>> =
        localDataSource.people

    suspend fun add(person: NewPerson) {
        localDataSource.add(person)
    }
}
