package com.jenzz.peoplenotes.ext

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.MutableStateFlow

class SaveableStateFlow<T> private constructor(
    private val savedStateHandle: SavedStateHandle,
    private val key: String,
    defaultValue: T,
    private val state: MutableStateFlow<T> = MutableStateFlow(
        savedStateHandle.get<T>(key) ?: defaultValue
    ),
) : MutableStateFlow<T> by state {

    companion object Factory {

        fun <T> create(
            savedStateHandle: SavedStateHandle,
            key: String,
            initialValue: T,
        ): SaveableStateFlow<T> =
            SaveableStateFlow(savedStateHandle, key, initialValue)
    }

    override var value: T
        get() = state.value
        set(value) {
            state.value = value
            savedStateHandle.set(key, value)
        }
}

fun <T> SavedStateHandle.saveableStateFlowOf(key: String, initialValue: T): SaveableStateFlow<T> =
    SaveableStateFlow.create(this, key, initialValue)
