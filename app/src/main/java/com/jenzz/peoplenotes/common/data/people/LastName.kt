package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.ext.NonEmptyString

@JvmInline
value class LastName(val value: NonEmptyString) : Comparable<LastName> {

    override fun toString(): String = value.toString()

    override fun compareTo(other: LastName): Int =
        value.compareTo(other.value)
}
