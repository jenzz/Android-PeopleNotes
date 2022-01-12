package com.jenzz.peoplenotes.feature.person_details.ui

import com.jenzz.peoplenotes.feature.person_details.data.PersonDetails

sealed class PersonDetailsUiState {

    object Loading : PersonDetailsUiState()

    data class Loaded(
        val personDetails: PersonDetails,
    ) : PersonDetailsUiState()
}
