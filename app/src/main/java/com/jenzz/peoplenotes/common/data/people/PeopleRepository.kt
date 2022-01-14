package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.feature.people.ui.PeopleSortBy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PeopleRepository @Inject constructor(
    private val localDataSource: PeopleDataSource,
) {

    fun getPerson(personId: PersonId): Flow<Person> =
        localDataSource.getPerson(personId)

    fun getAllPeople(sortBy: PeopleSortBy, filter: String): Flow<People> =
        localDataSource.getAllPeople(sortBy, filter)

    suspend fun add(person: NewPerson): Person =
        localDataSource.add(person)

    suspend fun delete(personId: PersonId) {
        localDataSource.delete(personId)
    }
}
