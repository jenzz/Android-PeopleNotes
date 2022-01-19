package com.jenzz.peoplenotes.feature.settings.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val localDataSource: SettingsDataSource,
) {

    fun observeSettings(): Flow<Settings> =
        localDataSource.observeSettings()

    suspend fun setTheme(theme: ThemePreference) {
        localDataSource.setTheme(theme)
    }
}
