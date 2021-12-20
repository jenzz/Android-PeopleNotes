package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.feature.home.ui.SortBy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PeopleRepository @Inject constructor(
    private val localDataSource: PeopleDataSource,
) {

    fun getPeople(sortBy: SortBy, filter: String): Flow<List<Person>> =
        localDataSource.getPeople(sortBy, filter)

    suspend fun add(person: NewPerson): Person =
        localDataSource.add(person)

    suspend fun delete(personId: PersonId) {
        localDataSource.delete(personId)
    }
}
