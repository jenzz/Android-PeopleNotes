package com.jenzz.peoplenotes.feature.settings.data

data class Settings(
    val theme: ThemePreference,
) {

    companion object {

        val DEFAULT = Settings(
            theme = ThemePreference.DEFAULT
        )
    }
}
