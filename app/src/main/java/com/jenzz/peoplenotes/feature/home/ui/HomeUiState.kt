package com.jenzz.peoplenotes.feature.home.ui

import com.jenzz.peoplenotes.feature.home.data.Home

sealed class HomeUiState {

    object Loading : HomeUiState()

    object Empty : HomeUiState()

    data class Loaded(
        val home: Home,
    ) : HomeUiState()
}
