package com.jenzz.peoplenotes.common.data.people

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.jenzz.peoplenotes.common.data.CoroutineDispatchers
import com.jenzz.peoplenotes.common.data.PersonQueries
import com.jenzz.peoplenotes.common.data.time.Clock
import com.jenzz.peoplenotes.ext.toEntity
import com.jenzz.peoplenotes.ext.toLocalDateTime
import com.jenzz.peoplenotes.ext.toNonEmptyString
import com.jenzz.peoplenotes.feature.people.ui.PeopleSortBy
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.rx3.asObservable
import com.squareup.sqldelight.runtime.rx3.mapToList
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface PeopleDataSource {

    fun observePerson(personId: PersonId): Flow<Person>

    fun observeAllPeople(sortBy: PeopleSortBy, filter: String): Observable<People>

    suspend fun get(personId: PersonId): Person

    suspend fun add(person: NewPerson): Person

    fun delete(personId: PersonId): Completable
}

class PeopleLocalDataSource @Inject constructor(
    private val personQueries: PersonQueries,
    private val dispatchers: CoroutineDispatchers,
    private val clock: Clock,
) : PeopleDataSource {

    private val toPerson =
        { id: Int, firstName: String, lastName: String, color: Int, lastModified: String ->
            Person(
                id = PersonId(id),
                firstName = FirstName(firstName.toNonEmptyString()),
                lastName = LastName(lastName.toNonEmptyString()),
                color = Color(color),
                lastModified = lastModified.toLocalDateTime(),
            )
        }

    override fun observePerson(personId: PersonId): Flow<Person> =
        personQueries
            .selectById(personId.value, toPerson)
            .asFlow()
            .mapToOne()

    override fun observeAllPeople(sortBy: PeopleSortBy, filter: String): Observable<People> {
        val comparator = when (sortBy) {
            PeopleSortBy.FirstName -> compareBy { person: Person -> person.firstName }
            PeopleSortBy.LastName -> compareBy { person: Person -> person.lastName }
            PeopleSortBy.LastModified -> compareByDescending { person: Person -> person.lastModified }
        }
        val filterSql = if (filter.isNotEmpty()) "%$filter%" else null
        return personQueries
            .selectAll(filterSql, toPerson)
            .asObservable()
            .mapToList()
            .delay(3, TimeUnit.SECONDS) // deliberate delay to test idling resource
            .map { persons ->
                People(
                    persons = persons.sortedWith(comparator),
                    totalCount = personQueries.count().executeAsOne().toInt(),
                )
            }
    }

    override suspend fun get(personId: PersonId): Person =
        withContext(dispatchers.Default) {
            personQueries
                .selectById(personId.value, toPerson)
                .executeAsOne()
        }

    override suspend fun add(person: NewPerson): Person =
        withContext(dispatchers.Default) {
            personQueries.transactionWithResult {
                personQueries.insert(
                    firstName = person.firstName.value.toString(),
                    lastName = person.lastName.value.toString(),
                    color = person.color.toArgb(),
                    lastModified = clock.now().toEntity(),
                )
                val rowId = personQueries.selectLastInsertRowId().executeAsOne()
                personQueries
                    .selectByRowId(rowId, toPerson)
                    .executeAsOne()
            }
        }

    override fun delete(personId: PersonId): Completable =
        Completable
            .fromAction { personQueries.delete(personId.value) }
            .subscribeOn(Schedulers.computation())
}
