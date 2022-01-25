package com.jenzz.peoplenotes.feature.people.data

import com.jenzz.peoplenotes.common.data.people.*
import com.jenzz.peoplenotes.common.ui.SortBy
import com.jenzz.peoplenotes.feature.people.ui.toPeopleSortBy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PeopleUseCases @Inject constructor(
    val observePeople: ObservePeopleUseCase,
    val deletePerson: DeletePersonUseCase,
    val deletePersonWithNotes: DeletePersonWithNotesUseCase,
)

class ObservePeopleUseCase @Inject constructor(
    private val peopleRepository: PeopleRepository,
) {

    operator fun invoke(sortBy: SortBy, filter: String): Flow<People> =
        peopleRepository.observeAllPeople(sortBy.toPeopleSortBy(), filter)
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

    suspend operator fun invoke(id: PersonId) {
        peopleAndNotesRepository.deleteWithNotes(id)
    }
}
