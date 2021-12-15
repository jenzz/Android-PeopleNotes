package com.jenzz.peoplenotes.common.data.di

import com.jenzz.peoplenotes.common.data.PeopleDataSource
import com.jenzz.peoplenotes.common.data.PeopleLocalDataSource
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
