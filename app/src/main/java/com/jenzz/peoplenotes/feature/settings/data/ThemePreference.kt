package com.jenzz.peoplenotes.feature.settings.data

import androidx.annotation.StringRes
import com.jenzz.peoplenotes.R

enum class ThemePreference(@StringRes val label: Int) {

    Light(R.string.light),
    Dark(R.string.dark),
    SystemDefault(R.string.system_default);

    companion object {

        val DEFAULT = SystemDefault
    }
}

fun ThemePreference.toEntity(): ThemePreferenceEntity =
    when (this) {
        ThemePreference.Light -> ThemePreferenceEntity.Light
        ThemePreference.Dark -> ThemePreferenceEntity.Dark
        ThemePreference.SystemDefault -> ThemePreferenceEntity.SystemDefault
    }
