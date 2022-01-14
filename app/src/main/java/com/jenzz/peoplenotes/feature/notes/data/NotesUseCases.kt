package com.jenzz.peoplenotes.feature.notes.data

import com.jenzz.peoplenotes.common.data.notes.NotesRepository
import com.jenzz.peoplenotes.common.data.people.PeopleRepository
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.feature.people.ui.PeopleSortBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class NotesUseCases @Inject constructor(
    val getNotesWithPerson: GetNotesWithPersonUseCase,
)

class GetNotesWithPersonUseCase @Inject constructor(
    private val peopleRepository: PeopleRepository,
    private val notesRepository: NotesRepository,
) {

    operator fun invoke(personId: PersonId): Flow<Notes> =
        peopleRepository
            .getPerson(personId)
            .combine(
                notesRepository.getNotes(personId)
            ) { person, notes ->
                Notes(
                    person = person,
                    notes = notes,
                )
            }

    operator fun invoke(
        personId: PersonId,
        sortBy: PeopleSortBy,
        filter: String
    ): Flow<Notes> =
        peopleRepository
            .getPerson(personId)
            .combine(
                notesRepository.getNotes(personId, sortBy, filter)
            ) { person, notes ->
                Notes(
                    person = person,
                    notes = notes,
                )
            }
}