package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.feature.people.ui.PeopleSortBy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PeopleRepository @Inject constructor(
    private val localDataSource: PeopleDataSource,
) {

    fun observePerson(personId: PersonId): Flow<Person> =
        localDataSource.observePerson(personId)

    fun observeAllPeople(sortBy: PeopleSortBy, filter: String): Observable<People> =
        localDataSource.observeAllPeople(sortBy, filter)

    suspend fun get(personId: PersonId): Person =
        localDataSource.get(personId)

    suspend fun add(person: NewPerson): Person =
        localDataSource.add(person)

    fun delete(personId: PersonId): Completable =
        localDataSource.delete(personId)
}
