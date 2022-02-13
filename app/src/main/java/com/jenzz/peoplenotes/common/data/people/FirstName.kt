package com.jenzz.peoplenotes.common.data.people

import com.jenzz.peoplenotes.ext.NonEmptyString

@JvmInline
value class FirstName(val value: NonEmptyString) : Comparable<FirstName> {

    override fun toString(): String = value.toString()

    override fun compareTo(other: FirstName): Int =
        value.compareTo(other.value)
}
