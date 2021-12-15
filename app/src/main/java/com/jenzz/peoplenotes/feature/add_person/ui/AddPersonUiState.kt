package com.jenzz.peoplenotes.feature.add_person.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddPersonUiState(
    val firstName: String,
    val lastName: String,
    val notes: String,
    val inputsEnabled: Boolean,
    val isUserAdded: Boolean,
) : Parcelable
