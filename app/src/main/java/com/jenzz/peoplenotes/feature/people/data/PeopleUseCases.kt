package com.jenzz.peoplenotes.feature.people.data

import com.jenzz.peoplenotes.common.data.people.*
import com.jenzz.peoplenotes.common.ui.SortBy
import com.jenzz.peoplenotes.feature.people.ui.toPeopleSortBy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class PeopleUseCases @Inject constructor(
    val observePeople: ObservePeopleUseCase,
    val deletePerson: DeletePersonUseCase,
    val deletePersonWithNotes: DeletePersonWithNotesUseCase,
)

class ObservePeopleUseCase @Inject constructor(
    private val peopleRepository: PeopleRepository,
) {

    operator fun invoke(sortBy: SortBy, filter: String): Observable<People> =
        peopleRepository.observeAllPeople(sortBy.toPeopleSortBy(), filter)
}

class DeletePersonUseCase @Inject constructor(
    private val peopleAndNotesRepository: PeopleAndNotesRepository,
) {

    operator fun invoke(id: PersonId): Single<DeletePersonResult> =
        peopleAndNotesRepository.delete(id)
}

class DeletePersonWithNotesUseCase @Inject constructor(
    private val peopleAndNotesRepository: PeopleAndNotesRepository,
) {

    operator fun invoke(id: PersonId): Completable =
        peopleAndNotesRepository.deleteWithNotes(id)
}
