package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.common.data.PersonQueries
import com.jenzz.peoplenotes.ext.NonEmptyString
import javax.inject.Inject

interface PeopleDataSource {

    suspend fun add(person: NewPerson): Person
}

class PeopleLocalDataSource @Inject constructor(
    private val personQueries: PersonQueries,
) : PeopleDataSource {

    override suspend fun add(person: NewPerson): Person =
        personQueries.transactionWithResult {
            personQueries.insert(person.firstName.toString(), person.lastName.toString())
            val rowId = personQueries.selectLastInsertRowId().executeAsOne()
            personQueries
                .selectByRowId(rowId) { id, firstName, lastName, lastModified ->
                    Person(
                        id = PersonId(id),
                        firstName = NonEmptyString(firstName),
                        lastName = NonEmptyString(lastName),
                        lastModified = lastModified,
                    )
                }
                .executeAsOne()
        }
}
