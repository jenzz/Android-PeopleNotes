package com.jenzz.peoplenotes.ext

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SavedStateHandleDelegate<T>(
    private val savedStateHandle: SavedStateHandle,
    private val key: String,
    defaultValue: T,
) : ReadWriteProperty<Any, T> {

    private val state = mutableStateOf(
        savedStateHandle.get<T>(key) ?: defaultValue
    )

    override fun getValue(thisRef: Any, property: KProperty<*>) =
        state.value

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        state.value = value
        savedStateHandle.set(key, value)
    }
}

fun <T> SavedStateHandle.mutableStateOf(
    defaultValue: T,
): PropertyDelegateProvider<Any, SavedStateHandleDelegate<T>> =
    PropertyDelegateProvider { _, property ->
        SavedStateHandleDelegate(
            savedStateHandle = this,
            key = property.name,
            defaultValue = defaultValue,
        )
    }
