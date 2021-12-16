package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.common.data.PersonQueries
import com.jenzz.peoplenotes.ext.NonEmptyString
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface PeopleDataSource {

    val people: Flow<List<Person>>

    suspend fun add(person: NewPerson): Person
}

class PeopleLocalDataSource @Inject constructor(
    private val personQueries: PersonQueries,
) : PeopleDataSource {

    private val toPerson =
        { id: Int, firstName: String, lastName: String, lastModified: String ->
            Person(
                id = PersonId(id),
                firstName = NonEmptyString(firstName),
                lastName = NonEmptyString(lastName),
                lastModified = lastModified,
            )
        }

    override val people: Flow<List<Person>> =
        personQueries
            .selectAll(toPerson)
            .asFlow()
            .mapToList()

    override suspend fun add(person: NewPerson): Person =
        personQueries.transactionWithResult {
            personQueries.insert(person.firstName.toString(), person.lastName.toString())
            val rowId = personQueries.selectLastInsertRowId().executeAsOne()
            personQueries
                .selectByRowId(rowId, toPerson)
                .executeAsOne()
        }
}
