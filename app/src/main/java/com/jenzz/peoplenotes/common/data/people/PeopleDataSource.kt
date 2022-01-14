package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.common.data.CoroutineDispatchers
import com.jenzz.peoplenotes.common.data.PersonQueries
import com.jenzz.peoplenotes.common.data.people.di.FirstName
import com.jenzz.peoplenotes.common.data.people.di.LastName
import com.jenzz.peoplenotes.common.data.time.Clock
import com.jenzz.peoplenotes.common.data.time.toEntity
import com.jenzz.peoplenotes.common.data.time.toLocalDateTime
import com.jenzz.peoplenotes.ext.toNonEmptyString
import com.jenzz.peoplenotes.feature.home.ui.PeopleSortBy
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface PeopleDataSource {

    fun getPerson(personId: PersonId): Flow<Person>

    fun getAllPeople(sortBy: PeopleSortBy, filter: String): Flow<People>

    suspend fun add(person: NewPerson): Person

    suspend fun delete(personId: PersonId)
}

class PeopleLocalDataSource @Inject constructor(
    private val personQueries: PersonQueries,
    private val dispatchers: CoroutineDispatchers,
    private val clock: Clock,
) : PeopleDataSource {

    private val toPerson =
        { id: Int, firstName: String, lastName: String, lastModified: String ->
            Person(
                id = PersonId(id),
                firstName = FirstName(firstName.toNonEmptyString()),
                lastName = LastName(lastName.toNonEmptyString()),
                lastModified = lastModified.toLocalDateTime(),
            )
        }

    override fun getPerson(personId: PersonId): Flow<Person> =
        personQueries
            .selectById(personId.value, toPerson)
            .asFlow()
            .mapToOne()

    override fun getAllPeople(sortBy: PeopleSortBy, filter: String): Flow<People> {
        val comparator = when (sortBy) {
            PeopleSortBy.FirstName -> compareBy { person: Person -> person.firstName }
            PeopleSortBy.LastName -> compareBy { person: Person -> person.lastName }
            PeopleSortBy.LastModified -> compareByDescending { person: Person -> person.lastModified }
        }
        val filterSql = if (filter.isNotEmpty()) "%$filter%" else null
        return personQueries
            .selectAll(filterSql, toPerson)
            .asFlow()
            .mapToList()
            .map { persons ->
                withContext(dispatchers.Default) {
                    People(
                        persons = persons.sortedWith(comparator),
                        totalCount = personQueries.count().executeAsOne().toInt(),
                    )
                }
            }
    }

    override suspend fun add(person: NewPerson): Person =
        withContext(dispatchers.Default) {
            personQueries.transactionWithResult {
                personQueries.insert(
                    firstName = person.firstName.value.toString(),
                    lastName = person.lastName.value.toString(),
                    lastModified = clock.now().toEntity(),
                )
                val rowId = personQueries.selectLastInsertRowId().executeAsOne()
                personQueries
                    .selectByRowId(rowId, toPerson)
                    .executeAsOne()
            }
        }

    override suspend fun delete(personId: PersonId) {
        withContext(dispatchers.Default) {
            personQueries.delete(personId.value)
        }
    }
}
