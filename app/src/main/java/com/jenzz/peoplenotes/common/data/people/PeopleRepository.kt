package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.feature.home.data.Home
import com.jenzz.peoplenotes.feature.home.ui.SortBy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PeopleRepository @Inject constructor(
    private val localDataSource: PeopleDataSource,
) {

    fun getPeople(sortBy: SortBy): Flow<Home> =
        localDataSource.getPeople(sortBy)

    suspend fun add(person: NewPerson) {
        localDataSource.add(person)
    }
}
