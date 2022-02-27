package com.jenzz.peoplenotes.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.feature.settings.data.SettingsUseCases
import com.jenzz.peoplenotes.feature.settings.data.ThemePreference
import com.jenzz.peoplenotes.feature.settings.ui.SettingsUiState.InitialLoad
import com.jenzz.peoplenotes.feature.settings.ui.SettingsUiState.Loaded
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val useCases: SettingsUseCases,
) : ViewModel() {

    val initialState = InitialLoad

    val state: StateFlow<SettingsUiState> =
        useCases
            .observeSettings()
            .map { settings -> Loaded(settings = settings) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = initialState
            )

    fun onThemeChange(theme: ThemePreference) {
        viewModelScope.launch {
            useCases.setTheme(theme)
        }
    }
}
