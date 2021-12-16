package com.jenzz.peoplenotes.feature.add_person.data

import androidx.annotation.StringRes
import com.jenzz.peoplenotes.ext.NonEmptyString
import com.jenzz.peoplenotes.ext.toNonEmptyStringOrNull
import com.jenzz.peoplenotes.feature.add_person.data.AddPersonNameValidationResult.Invalid
import com.jenzz.peoplenotes.feature.add_person.data.AddPersonNameValidationResult.Valid
import javax.inject.Inject

class AddPersonNameValidator @Inject constructor() {

    fun validate(name: String, @StringRes error: Int): AddPersonNameValidationResult {
        val validatedName = name.toNonEmptyStringOrNull()
        return if (validatedName != null)
            Valid(validatedName)
        else
            Invalid(error)
    }
}

sealed class AddPersonNameValidationResult {

    data class Valid(val name: NonEmptyString) : AddPersonNameValidationResult()

    data class Invalid(@StringRes val error: Int) : AddPersonNameValidationResult()

    val errorOrNull: Int?
        get() = if (this is Invalid) error else null
}
