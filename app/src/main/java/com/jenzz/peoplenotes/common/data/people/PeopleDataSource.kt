package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.common.data.PersonQueries
import com.jenzz.peoplenotes.common.data.people.di.FirstName
import com.jenzz.peoplenotes.common.data.people.di.LastName
import com.jenzz.peoplenotes.ext.toNonEmptyString
import javax.inject.Inject

interface PeopleDataSource {

    suspend fun add(person: NewPerson): Person

    suspend fun delete(personId: PersonId)
}

class PeopleLocalDataSource @Inject constructor(
    private val personQueries: PersonQueries,
) : PeopleDataSource {

    private val toPerson =
        { id: Int, firstName: String, lastName: String ->
            Person(
                id = PersonId(id),
                firstName = FirstName(firstName.toNonEmptyString()),
                lastName = LastName(lastName.toNonEmptyString()),
            )
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
