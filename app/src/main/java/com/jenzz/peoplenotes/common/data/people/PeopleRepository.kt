package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.feature.people.ui.PeopleSortBy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PeopleRepository @Inject constructor(
    private val localDataSource: PeopleDataSource,
) {

    fun observePerson(personId: PersonId): Flow<Person> =
        localDataSource.observePerson(personId)

    fun observeAllPeople(sortBy: PeopleSortBy, filter: String): Flow<People> =
        localDataSource.observeAllPeople(sortBy, filter)

    suspend fun get(personId: PersonId): Person =
        localDataSource.get(personId)

    suspend fun add(person: NewPerson): Person =
        localDataSource.add(person)

    suspend fun delete(personId: PersonId): Person =
        localDataSource.delete(personId)
}
