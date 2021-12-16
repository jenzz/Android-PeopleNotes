package com.jenzz.peoplenotes.common.data.notes.di

import com.jenzz.peoplenotes.common.data.notes.NotesDataSource
import com.jenzz.peoplenotes.common.data.notes.NotesLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NotesModule {

    @Binds
    fun NotesLocalDataSource.bind(): NotesDataSource
}
