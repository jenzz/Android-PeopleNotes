package com.jenzz.peoplenotes.feature.settings

enum class ThemePreferenceEntity(val id: ThemeIdEntity) {

    Light(id = ThemeIdEntity(1)),
    Dark(id = ThemeIdEntity(2)),
    SystemDefault(id = ThemeIdEntity(3)),
}

@JvmInline
value class ThemeIdEntity(val value: Int)

fun ThemeIdEntity.toDomain(): ThemePreference =
    when (this) {
        ThemePreferenceEntity.Light.id -> ThemePreference.Light
        ThemePreferenceEntity.Dark.id -> ThemePreference.Dark
        ThemePreferenceEntity.SystemDefault.id -> ThemePreference.SystemDefault
        else -> error("Unknown theme with id $this.")
    }
