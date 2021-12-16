package com.jenzz.peoplenotes.feature.home.data

import com.jenzz.peoplenotes.common.data.people.PeopleRepository
import com.jenzz.peoplenotes.common.data.people.Person
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeUseCases @Inject constructor(
    val getPeople: GetPeopleUseCase,
)

class GetPeopleUseCase @Inject constructor(
    private val peopleRepository: PeopleRepository,
) {

    operator fun invoke(): Flow<List<Person>> =
        peopleRepository.people
}
