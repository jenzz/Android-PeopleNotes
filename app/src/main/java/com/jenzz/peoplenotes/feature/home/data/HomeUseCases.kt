package com.jenzz.peoplenotes.feature.home.data

import com.jenzz.peoplenotes.common.data.notes.NotesRepository
import com.jenzz.peoplenotes.common.data.people.PeopleRepository
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.common.data.people.PersonId
import com.jenzz.peoplenotes.feature.home.ui.SortBy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeUseCases @Inject constructor(
    val getPeople: GetPeopleUseCase,
    val deletePerson: DeletePersonUseCase,
)

class GetPeopleUseCase @Inject constructor(
    private val peopleRepository: PeopleRepository,
) {

    operator fun invoke(sortBy: SortBy, filter: String): Flow<List<Person>> =
        peopleRepository.getPeople(sortBy, filter)
}

class DeletePersonUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
    private val peopleRepository: PeopleRepository,
) {

    suspend operator fun invoke(personId: PersonId) {
        notesRepository.deleteAllNotesByPerson(personId)
        peopleRepository.delete(personId)
    }
}
