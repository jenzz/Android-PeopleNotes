package com.jenzz.peoplenotes.common.data.people

import android.os.Parcelable
import com.jenzz.peoplenotes.ext.NonEmptyString
import kotlinx.parcelize.Parcelize

@Parcelize
@JvmInline
value class FirstName(val value: NonEmptyString) : Comparable<FirstName>, Parcelable {

    override fun toString(): String = value.toString()

    override fun compareTo(other: FirstName): Int =
        value.compareTo(other.value)
}
