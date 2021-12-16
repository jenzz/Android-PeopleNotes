package com.jenzz.peoplenotes.common.data.people.di

import com.jenzz.peoplenotes.common.data.people.PeopleDataSource
import com.jenzz.peoplenotes.common.data.people.PeopleLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface PeopleModule {

    @Binds
    fun PeopleLocalDataSource.bind(): PeopleDataSource
}
