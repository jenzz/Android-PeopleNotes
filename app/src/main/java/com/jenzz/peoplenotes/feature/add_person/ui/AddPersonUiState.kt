package com.jenzz.peoplenotes.feature.add_person.ui

import android.os.Parcelable
import com.jenzz.peoplenotes.common.ui.TextFieldUiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddPersonUiState(
    val firstName: TextFieldUiState,
    val lastName: TextFieldUiState,
    val note: String,
    val inputsEnabled: Boolean,
    val isUserAdded: Boolean,
) : Parcelable
