package com.jenzz.peoplenotes.feature.add_person.data

import com.jenzz.peoplenotes.common.data.PeopleRepository
import com.jenzz.peoplenotes.common.data.Person
import javax.inject.Inject

class AddPersonUseCases @Inject constructor(
    val addPerson: AddPersonUseCase,
)

class AddPersonUseCase @Inject constructor(
    private val peopleRepository: PeopleRepository,
) {

    suspend operator fun invoke(person: Person) {
        peopleRepository.add(person)
    }
}
