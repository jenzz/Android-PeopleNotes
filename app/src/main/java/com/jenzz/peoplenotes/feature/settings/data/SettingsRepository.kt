package com.jenzz.peoplenotes.feature.settings.data

import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val localDataSource: SettingsDataSource,
) {

    val settings = localDataSource.settings

    suspend fun setTheme(theme: ThemePreference) {
        localDataSource.setTheme(theme)
    }
}
