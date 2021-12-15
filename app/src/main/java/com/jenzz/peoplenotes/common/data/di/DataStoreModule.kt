package com.jenzz.peoplenotes.common.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

typealias PreferencesDataStore = DataStore<Preferences>

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): PreferencesDataStore =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("people_notes")
        }
}
