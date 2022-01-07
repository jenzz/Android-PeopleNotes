package com.jenzz.peoplenotes.feature.settings.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.feature.settings.data.SettingsUseCases
import com.jenzz.peoplenotes.feature.settings.data.ThemePreference
import com.jenzz.peoplenotes.feature.settings.ui.SettingsUiState.Loading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val useCases: SettingsUseCases,
) : ViewModel() {

    private val _state = mutableStateOf<SettingsUiState>(Loading)
    val state: State<SettingsUiState> = _state

    init {
        viewModelScope.launch {
            useCases
                .getSettings()
                .collect { settings ->
                    _state.value = SettingsUiState.Loaded(settings)
                }
        }
    }

    fun onThemeChange(theme: ThemePreference) {
        viewModelScope.launch {
            useCases.setTheme(theme)
        }
    }
}
