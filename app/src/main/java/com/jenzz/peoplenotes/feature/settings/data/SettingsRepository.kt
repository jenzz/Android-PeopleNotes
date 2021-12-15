package com.jenzz.peoplenotes.feature.settings.data

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.jenzz.peoplenotes.common.data.di.PreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val dataStore: PreferencesDataStore,
) {

    private companion object Keys {

        private val THEME = intPreferencesKey("theme")
    }

    val settings: Flow<Settings>
        get() = dataStore.data.map { preferences ->
            Settings(
                theme = preferences.theme,
            )
        }

    suspend fun setTheme(theme: ThemePreference) {
        dataStore.edit { preferences ->
            preferences[THEME] = theme.toEntity().id.value
        }
    }

    private val Preferences.theme: ThemePreference
        get() {
            val id = get(THEME)
            return if (id != null)
                ThemeIdEntity(id).toDomain()
            else
                ThemePreference.SystemDefault
        }
}
