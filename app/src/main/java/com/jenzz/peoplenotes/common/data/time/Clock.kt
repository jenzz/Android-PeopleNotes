package com.jenzz.peoplenotes.common.data.time

import java.time.LocalDateTime
import javax.inject.Inject

interface Clock {

    fun now(): LocalDateTime
}

class SystemClock @Inject constructor() : Clock {

    override fun now(): LocalDateTime =
        LocalDateTime.now()
}