package com.jenzz.peoplenotes.feature.notes.di

import androidx.lifecycle.SavedStateHandle
import com.jenzz.peoplenotes.feature.destinations.NotesScreenDestination
import com.jenzz.peoplenotes.feature.notes.ui.NotesScreenNavArgs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object NotesScreenModule {

    @Provides
    fun provideNavArgs(savedStateHandle: SavedStateHandle): NotesScreenNavArgs =
        NotesScreenDestination.argsFrom(savedStateHandle)
}
