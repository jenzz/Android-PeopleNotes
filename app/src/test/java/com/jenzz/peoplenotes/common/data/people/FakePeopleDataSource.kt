package com.jenzz.peoplenotes.common.data.people

import com.jakewharton.rxrelay3.BehaviorRelay
import com.jenzz.peoplenotes.feature.people.ui.PeopleSortBy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

class FakePeopleDataSource : PeopleDataSource {

    private val people = BehaviorRelay.create<People>()

    override fun observePerson(personId: PersonId): Flow<Person> {
        TODO("Not yet implemented")
    }

    override fun observeAllPeople(sortBy: PeopleSortBy, filter: String): Observable<People> =
        people

    override suspend fun get(personId: PersonId): Person {
        TODO("Not yet implemented")
    }

    override suspend fun add(person: NewPerson): Person {
        TODO("Not yet implemented")
    }

    override fun delete(personId: PersonId): Completable {
        TODO("Not yet implemented")
    }

    fun emit(people: People) {
        this.people.accept(people)
    }
}
