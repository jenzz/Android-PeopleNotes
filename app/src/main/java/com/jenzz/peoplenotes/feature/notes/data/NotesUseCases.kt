package com.jenzz.peoplenotes.feature.notes.data

import com.jenzz.peoplenotes.common.data.notes.NotesRepository
import com.jenzz.peoplenotes.common.data.people.PeopleRepository
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.common.ui.SortBy
import com.jenzz.peoplenotes.feature.notes.ui.toNotesSortBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class NotesUseCases @Inject constructor(
    val observeNotesWithPerson: ObserveNotesWithPersonUseCase,
)

class ObserveNotesWithPersonUseCase @Inject constructor(
    private val peopleRepository: PeopleRepository,
    private val notesRepository: NotesRepository,
) {

    operator fun invoke(
        personId: PersonId,
        sortBy: SortBy,
        filter: String,
    ): Flow<Notes> =
        peopleRepository
            .observePerson(personId)
            .combine(
                notesRepository.observeNotes(personId, sortBy.toNotesSortBy(), filter)
            ) { person, notes ->
                Notes(
                    person = person,
                    notes = notes,
                )
            }
}
