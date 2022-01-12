package com.jenzz.peoplenotes.feature.person_details.data

import com.jenzz.peoplenotes.common.data.notes.Note
import com.jenzz.peoplenotes.common.data.notes.NotesRepository
import com.jenzz.peoplenotes.common.data.people.PersonId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PersonDetailsUseCases @Inject constructor(
    val getNotesUseCase: GetNotesUseCase,
)

class GetNotesUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {

    operator fun invoke(personId: PersonId): Flow<List<Note>> =
        notesRepository.getNotes(personId)
}