package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.common.data.PersonQueries
import com.jenzz.peoplenotes.common.data.notes.Note
import com.jenzz.peoplenotes.common.data.people.di.FirstName
import com.jenzz.peoplenotes.common.data.people.di.LastName
import com.jenzz.peoplenotes.ext.toNonEmptyString
import com.jenzz.peoplenotes.feature.home.ui.SortBy
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PeopleDataSource {

    fun getAllPeople(sortBy: SortBy, filter: String): Flow<List<Person>>

    suspend fun add(person: NewPerson): Person

    suspend fun delete(personId: PersonId)
}

class PeopleLocalDataSource @Inject constructor(
    private val personQueries: PersonQueries,
) : PeopleDataSource {

    private val toPerson =
        { id: Int, firstName: String, lastName: String, lastModified: String ->
            Person(
                id = PersonId(id),
                firstName = FirstName(firstName.toNonEmptyString()),
                lastName = LastName(lastName.toNonEmptyString()),
                lastModified = lastModified,
            )
        }

    override fun getAllPeople(sortBy: SortBy, filter: String): Flow<List<Person>> {
        val orderBy = when (sortBy) {
            SortBy.FirstName -> compareBy { person: Person -> person.firstName }
            SortBy.LastName -> compareBy { person: Person -> person.lastName }
            SortBy.LastModified -> compareByDescending { person: Person -> person.lastModified }
        }
        val filterSql = if (filter.isNotEmpty()) "%$filter%" else null
        return personQueries
            .selectAll(filterSql, toPerson)
            .asFlow()
            .mapToList()
            .map { notes -> notes.sortedWith(orderBy) }
    }

    override suspend fun add(person: NewPerson): Person =
        personQueries.transactionWithResult {
            personQueries.insert(
                firstName = person.firstName.value.toString(),
                lastName = person.lastName.value.toString(),
            )
            val rowId = personQueries.selectLastInsertRowId().executeAsOne()
            personQueries
                .selectByRowId(rowId, toPerson)
                .executeAsOne()
        }

    override suspend fun delete(personId: PersonId) {
        personQueries.delete(personId.value)
    }
}
