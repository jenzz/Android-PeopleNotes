package com.jenzz.peoplenotes.ext

import android.os.Parcelable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PartialSaveStateHandleDelegate<T : PartialSavedState<T, R>, R : Parcelable>(
    private val savedStateHandle: SavedStateHandle,
    private val key: String,
    defaultValue: T,
) : ReadWriteProperty<Any, T> {

    private val state = mutableStateOf(defaultValue)

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        val savedState = savedStateHandle.get<R>(key)
        return if (savedState != null)
            state.value.mergeWithSavedState(savedState)
        else
            state.value
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        state.value = value
        savedStateHandle.set(key, value.savedState)
    }
}

fun <T : PartialSavedState<T, R>, R : Parcelable> SavedStateHandle.mutableStateOf(
    defaultValue: T,
): PropertyDelegateProvider<Any, PartialSaveStateHandleDelegate<T, R>> =
    PropertyDelegateProvider { _, property ->
        PartialSaveStateHandleDelegate(
            savedStateHandle = this,
            key = property.name,
            defaultValue = defaultValue,
        )
    }

interface PartialSavedState<State, SavedState : Parcelable> {

    val savedState: SavedState

    fun mergeWithSavedState(savedState: SavedState): State
}
