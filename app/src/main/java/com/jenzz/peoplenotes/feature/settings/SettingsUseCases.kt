package com.jenzz.peoplenotes.feature.settings

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsUseCases @Inject constructor(
    val getSettings: GetSettingsUseCase,
    val setTheme: SetThemeUseCase,
)

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {

    operator fun invoke(): Flow<Settings> =
        settingsRepository.settings
}

class SetThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {

    suspend operator fun invoke(theme: ThemePreference) {
        settingsRepository.setTheme(theme)
    }
}
