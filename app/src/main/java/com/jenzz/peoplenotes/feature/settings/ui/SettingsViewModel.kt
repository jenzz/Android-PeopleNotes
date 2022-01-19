package com.jenzz.peoplenotes.feature.settings.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jenzz.peoplenotes.feature.settings.data.SettingsUseCases
import com.jenzz.peoplenotes.feature.settings.data.ThemePreference
import com.jenzz.peoplenotes.feature.settings.ui.SettingsUiState.InitialLoad
import com.jenzz.peoplenotes.feature.settings.ui.SettingsUiState.Loaded
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val useCases: SettingsUseCases,
) : ViewModel() {

    var state by mutableStateOf<SettingsUiState>(InitialLoad)
        private set

    init {
        viewModelScope.launch {
            useCases
                .observeSettings()
                .collect { settings ->
                    state = Loaded(
                        settings = settings
                    )
                }
        }
    }

    fun onThemeChange(theme: ThemePreference) {
        viewModelScope.launch {
            useCases.setTheme(theme)
        }
    }
}
