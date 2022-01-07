package com.jenzz.peoplenotes.feature.home.data

import com.jenzz.peoplenotes.common.data.people.*
import com.jenzz.peoplenotes.feature.home.ui.SortBy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeUseCases @Inject constructor(
    val getPeople: GetPeopleUseCase,
    val deletePerson: DeletePersonUseCase,
    val deletePersonWithNotes: DeletePersonWithNotesUseCase,
)

class GetPeopleUseCase @Inject constructor(
    private val peopleRepository: PeopleRepository,
) {

    operator fun invoke(sortBy: SortBy, filter: String): Flow<People> =
        peopleRepository.getAllPeople(sortBy, filter)
}

class DeletePersonUseCase @Inject constructor(
    private val peopleAndNotesRepository: PeopleAndNotesRepository,
) {

    suspend operator fun invoke(id: PersonId): DeletePersonResult =
        peopleAndNotesRepository.delete(id)
}

class DeletePersonWithNotesUseCase @Inject constructor(
    private val peopleAndNotesRepository: PeopleAndNotesRepository,
) {

    suspend operator fun invoke(id: PersonId) =
        peopleAndNotesRepository.deleteWithNotes(id)
}
