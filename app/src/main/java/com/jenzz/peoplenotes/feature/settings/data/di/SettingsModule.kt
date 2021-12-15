package com.jenzz.peoplenotes.feature.settings.data.di

import com.jenzz.peoplenotes.feature.settings.data.SettingsDataSource
import com.jenzz.peoplenotes.feature.settings.data.SettingsLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface SettingsModule {

    @Binds
    fun SettingsLocalDataSource.bind(): SettingsDataSource
}
