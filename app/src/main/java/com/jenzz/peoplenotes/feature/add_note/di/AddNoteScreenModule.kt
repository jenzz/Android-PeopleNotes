package com.jenzz.peoplenotes.feature.add_note.di

import androidx.lifecycle.SavedStateHandle
import com.jenzz.peoplenotes.feature.add_note.ui.AddNoteScreenNavArgs
import com.jenzz.peoplenotes.feature.destinations.AddNoteScreenDestination
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object AddNoteScreenModule {

    @Provides
    fun provideNavArgs(savedStateHandle: SavedStateHandle): AddNoteScreenNavArgs =
        AddNoteScreenDestination.argsFrom(savedStateHandle)
}
