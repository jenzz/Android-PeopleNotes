package com.jenzz.peoplenotes.common.ui

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class TextFieldUiState(
    val value: String,
    @StringRes val error: Int? = null,
) : Parcelable {

    @IgnoredOnParcel
    val isError: Boolean = error != null

    @StringRes
    fun requireError(): Int = requireNotNull(error) { "Required error was null." }
}
