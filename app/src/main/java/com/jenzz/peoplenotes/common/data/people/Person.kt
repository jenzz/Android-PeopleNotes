package com.jenzz.peoplenotes.common.data.people

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

data class Person(
    val id: PersonId,
    val firstName: FirstName,
    val lastName: LastName,
    val color: Color,
    val lastModified: LocalDateTime,
) {

    val fullName: String = "${firstName.value} ${lastName.value}"

    val firstNameLetter: String = firstName.toString().first().uppercase()
}

fun Person.simplified(): PersonSimplified =
    PersonSimplified(
        id = id,
        firstName = firstName,
        lastName = lastName,
    )

@Parcelize
data class PersonSimplified(
    val id: PersonId,
    val firstName: FirstName,
    val lastName: LastName,
) : Parcelable {

    @IgnoredOnParcel
    val fullName: String = "${firstName.value} ${lastName.value}"
}

@JvmInline
value class PersonId(val value: Int) : Parcelable {

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(value)
    }

    companion object CREATOR : Parcelable.Creator<PersonId> {

        override fun createFromParcel(parcel: Parcel): PersonId =
            PersonId(parcel.readInt())

        override fun newArray(size: Int): Array<PersonId?> =
            arrayOfNulls(size)
    }
}
