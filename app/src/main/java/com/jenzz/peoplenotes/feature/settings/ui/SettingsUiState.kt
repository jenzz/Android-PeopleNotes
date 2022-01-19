package com.jenzz.peoplenotes.feature.settings.ui

import com.jenzz.peoplenotes.feature.settings.data.Settings

sealed class SettingsUiState {

    object InitialLoad : SettingsUiState()

    data class Loaded(
        val settings: Settings,
    ) : SettingsUiState()
}
