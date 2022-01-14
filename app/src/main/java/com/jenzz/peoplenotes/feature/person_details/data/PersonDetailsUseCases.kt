package com.jenzz.peoplenotes.feature.person_details.data

import com.jenzz.peoplenotes.common.data.notes.NotesRepository
import com.jenzz.peoplenotes.common.data.people.PeopleRepository
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.feature.people.ui.PeopleSortBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class PersonDetailsUseCases @Inject constructor(
    val getPersonDetails: GetPersonDetails,
)

class GetPersonDetails @Inject constructor(
    private val peopleRepository: PeopleRepository,
    private val notesRepository: NotesRepository,
) {

    operator fun invoke(personId: PersonId): Flow<PersonDetails> =
        peopleRepository
            .getPerson(personId)
            .combine(
                notesRepository.getNotes(personId)
            ) { person, notes ->
                PersonDetails(
                    person = person,
                    notes = notes,
                )
            }

    operator fun invoke(
        personId: PersonId,
        sortBy: PeopleSortBy,
        filter: String
    ): Flow<PersonDetails> =
        peopleRepository
            .getPerson(personId)
            .combine(
                notesRepository.getNotes(personId, sortBy, filter)
            ) { person, notes ->
                PersonDetails(
                    person = person,
                    notes = notes,
                )
            }
}