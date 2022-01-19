package com.jenzz.peoplenotes.feature.settings.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsUseCases @Inject constructor(
    val observeSettings: ObserveSettingsUseCase,
    val setTheme: SetThemeUseCase,
)

class ObserveSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {

    operator fun invoke(): Flow<Settings> =
        settingsRepository.observeSettings()
}

class SetThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {

    suspend operator fun invoke(theme: ThemePreference) {
        settingsRepository.setTheme(theme)
    }
}
