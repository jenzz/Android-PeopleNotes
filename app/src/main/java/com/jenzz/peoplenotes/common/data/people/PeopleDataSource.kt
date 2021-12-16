package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.common.data.PersonQueries
import com.jenzz.peoplenotes.common.data.people.di.FirstName
import com.jenzz.peoplenotes.common.data.people.di.LastName
import com.jenzz.peoplenotes.ext.toNonEmptyString
import com.jenzz.peoplenotes.feature.home.data.Home
import com.jenzz.peoplenotes.feature.home.ui.SortBy
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PeopleDataSource {

    fun getPeople(sortBy: SortBy): Flow<Home>

    suspend fun add(person: NewPerson): Person
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

    override fun getPeople(sortBy: SortBy): Flow<Home> {
        val selectAll = when (sortBy) {
            SortBy.LastModified -> personQueries.selectAllSortedByLastModified(toPerson)
            SortBy.FirstName -> personQueries.selectAllSortedByFirstName(toPerson)
            SortBy.LastName -> personQueries.selectAllSortedByLastName(toPerson)
        }
        return selectAll
            .asFlow()
            .mapToList()
            .map { people -> Home(sortBy, people) }
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
}
