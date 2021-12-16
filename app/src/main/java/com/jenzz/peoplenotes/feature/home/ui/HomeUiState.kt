package com.jenzz.peoplenotes.feature.home.ui

import com.jenzz.peoplenotes.feature.home.data.Home

sealed class HomeUiState {

    object Loading : HomeUiState()

    data class Loaded(
        val home: Home,
    ) : HomeUiState()
}
