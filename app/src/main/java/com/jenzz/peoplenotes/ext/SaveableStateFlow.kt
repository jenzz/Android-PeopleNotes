package com.jenzz.peoplenotes.ext

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SaveableStateFlow<T>(
    private val savedStateHandle: SavedStateHandle,
    private val key: String,
    defaultValue: T,
) {

    private val _state = MutableStateFlow(
        savedStateHandle.get<T>(key) ?: defaultValue
    )

    var value: T
        get() = _state.value
        set(value) {
            _state.value = value
            savedStateHandle.set(key, value)
        }

    fun asStateFlow(): StateFlow<T> = _state.asStateFlow()
}

fun <T> SavedStateHandle.saveableStateFlowOf(key: String, initialValue: T): SaveableStateFlow<T> =
    SaveableStateFlow(this, key, initialValue)
