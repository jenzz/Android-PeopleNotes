package com.jenzz.peoplenotes.feature.home.ui

import com.jenzz.peoplenotes.common.data.people.Person

sealed class HomeUiState {

    object Loading : HomeUiState()

    data class Loaded(
        val people: List<Person>,
    ) : HomeUiState()
}
