package com.jenzz.peoplenotes.feature.person_details.ui

import com.jenzz.peoplenotes.feature.settings.data.Settings

sealed class PersonDetailsUiState {

    object Loading : PersonDetailsUiState()

    data class Loaded(
        val settings: Settings,
    ) : PersonDetailsUiState()
}
