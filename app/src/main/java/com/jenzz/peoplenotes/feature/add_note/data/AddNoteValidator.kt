package com.jenzz.peoplenotes.feature.add_note.data

import com.jenzz.peoplenotes.ext.NonEmptyString
import com.jenzz.peoplenotes.ext.toNonEmptyStringOrNull
import com.jenzz.peoplenotes.feature.add_note.data.AddNoteValidationResult.Invalid
import com.jenzz.peoplenotes.feature.add_note.data.AddNoteValidationResult.Valid
import javax.inject.Inject

class AddNoteValidator @Inject constructor() {

    fun validate(note: String): AddNoteValidationResult {
        val validatedNote = note.toNonEmptyStringOrNull()
        return if (validatedNote != null)
            Valid(validatedNote)
        else
            Invalid
    }
}

sealed class AddNoteValidationResult {

    data class Valid(val note: NonEmptyString) : AddNoteValidationResult()

    object Invalid : AddNoteValidationResult()
}
