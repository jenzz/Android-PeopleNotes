package com.jenzz.peoplenotes.feature.person_details.data

import com.jenzz.peoplenotes.common.data.notes.NotesRepository
import com.jenzz.peoplenotes.common.data.people.PeopleRepository
import com.jenzz.peoplenotes.common.data.people.PersonId
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
}