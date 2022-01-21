package com.jenzz.peoplenotes.common.data.notes

import android.os.Parcel
import android.os.Parcelable
import com.jenzz.peoplenotes.common.data.people.Person
import com.jenzz.peoplenotes.ext.NonEmptyString
import java.time.LocalDateTime

data class Note(
    val id: NoteId,
    val text: NonEmptyString,
    val lastModified: LocalDateTime,
    val person: Person,
)

@JvmInline
value class NoteId(val value: Int) : Parcelable {

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(value)
    }

    companion object CREATOR : Parcelable.Creator<NoteId> {

        override fun createFromParcel(parcel: Parcel): NoteId =
            NoteId(parcel.readInt())

        override fun newArray(size: Int): Array<NoteId?> =
            arrayOfNulls(size)
    }
}
