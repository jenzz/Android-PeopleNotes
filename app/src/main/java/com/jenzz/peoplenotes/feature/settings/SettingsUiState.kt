package com.jenzz.peoplenotes.feature.settings

sealed class SettingsUiState {

    object Loading : SettingsUiState()

    data class Loaded(
        val settings: Settings,
    ) : SettingsUiState()
}
