package com.jenzz.peoplenotes.common.data.people

import android.os.Parcel
import android.os.Parcelable
import com.jenzz.peoplenotes.common.data.people.di.FirstName
import com.jenzz.peoplenotes.common.data.people.di.LastName
import java.time.LocalDateTime

data class Person(
    val id: PersonId,
    val firstName: FirstName,
    val lastName: LastName,
    val lastModified: LocalDateTime,
) {

    val fullName: String = "${firstName.value} ${lastName.value}"

    val firstNameLetter: Char = firstName.toString().first()
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
