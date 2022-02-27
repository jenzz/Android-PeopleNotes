package com.jenzz.peoplenotes.common.data.people

import android.os.Parcelable
import com.jenzz.peoplenotes.ext.NonEmptyString
import kotlinx.parcelize.Parcelize

@Parcelize
@JvmInline
value class LastName(val value: NonEmptyString) : Comparable<LastName>, Parcelable {

    override fun toString(): String = value.toString()

    override fun compareTo(other: LastName): Int =
        value.compareTo(other.value)
}
