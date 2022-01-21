package com.jenzz.peoplenotes.feature.settings.di

import com.jenzz.peoplenotes.feature.settings.data.SettingsDataSource
import com.jenzz.peoplenotes.feature.settings.data.SettingsLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface SettingsModule {

    @Binds
    fun SettingsLocalDataSource.bind(): SettingsDataSource
}
