package com.jenzz.peoplenotes.common.data.people.di

import com.jenzz.peoplenotes.ext.NonEmptyString

@JvmInline
value class FirstName(val value: NonEmptyString) : Comparable<FirstName> {

    override fun compareTo(other: FirstName): Int =
        value.compareTo(other.value)
}
