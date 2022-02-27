package com.jenzz.peoplenotes.ext

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@JvmInline
value class NonEmptyString(val value: String) : Comparable<NonEmptyString>, Parcelable {

    init {
        require(value.isNotEmpty()) { "String must not be empty." }
    }

    override fun toString(): String = value

    override fun compareTo(other: NonEmptyString): Int =
        value.compareTo(other.value)
}

fun String.toNonEmptyString(): NonEmptyString = NonEmptyString(this)

fun String.toNonEmptyStringOrNull(): NonEmptyString? =
    try {
        toNonEmptyString()
    } catch (e: IllegalArgumentException) {
        null
    }
