package com.jenzz.peoplenotes.common.data.people.di

import com.jenzz.peoplenotes.ext.NonEmptyString

@JvmInline
value class LastName(val value: NonEmptyString) : Comparable<LastName> {

    override fun compareTo(other: LastName): Int =
        value.compareTo(other.value)
}
