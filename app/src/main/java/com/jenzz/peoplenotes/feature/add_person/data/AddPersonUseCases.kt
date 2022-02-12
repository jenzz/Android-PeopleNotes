package com.jenzz.peoplenotes.feature.add_person.data

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.common.data.notes.NewNote
import com.jenzz.peoplenotes.common.data.people.FirstName
import com.jenzz.peoplenotes.common.data.people.LastName
import com.jenzz.peoplenotes.common.data.people.NewPerson
import com.jenzz.peoplenotes.common.data.people.PeopleAndNotesRepository
import com.jenzz.peoplenotes.ext.NonEmptyString
import com.jenzz.peoplenotes.ext.random
import com.jenzz.peoplenotes.ext.toNonEmptyStringOrNull
import com.jenzz.peoplenotes.feature.add_person.data.AddPersonNameValidationResult.Invalid
import com.jenzz.peoplenotes.feature.add_person.data.AddPersonNameValidationResult.Valid
import javax.inject.Inject

class AddPersonUseCases @Inject constructor(
    val addPersonWithNote: AddPersonWithNoteUseCase,
)

class AddPersonWithNoteUseCase @Inject constructor(
    private val nameValidator: AddPersonNameValidator,
    private val peopleAndNotesRepository: PeopleAndNotesRepository,
) {

    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        note: String,
    ): AddPersonResult {
        val validatedFirstName =
            nameValidator.validate(firstName, R.string.first_name_cannot_be_empty)
        val validatedLastName =
            nameValidator.validate(lastName, R.string.last_name_cannot_be_empty)
        if (validatedFirstName is Invalid || validatedLastName is Invalid) {
            return AddPersonResult.Error(
                firstNameError = validatedFirstName.errorOrNull,
                lastNameError = validatedLastName.errorOrNull,
            )
        }
        addPersonAndNote(
            firstName = (validatedFirstName as Valid).name,
            lastName = (validatedLastName as Valid).name,
            note = note
        )
        return AddPersonResult.Success
    }

    private suspend fun addPersonAndNote(
        firstName: NonEmptyString,
        lastName: NonEmptyString,
        note: String,
    ) {
        val newPerson = NewPerson(
            firstName = FirstName(firstName),
            lastName = LastName(lastName),
            color = Color.random(),
        )
        val newNote = note.toNonEmptyStringOrNull()?.let { NewNote(it) }
        peopleAndNotesRepository.add(newPerson, newNote)
    }
}

sealed class AddPersonResult {

    object Success : AddPersonResult()

    data class Error(
        @StringRes val firstNameError: Int?,
        @StringRes val lastNameError: Int?,
    ) : AddPersonResult()
}
