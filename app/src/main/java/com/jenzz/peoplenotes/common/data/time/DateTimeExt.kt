package com.jenzz.peoplenotes.common.data.time

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun CharSequence.toLocalDateTime(
    formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
): LocalDateTime =
    LocalDateTime.parse(this, formatter)

fun LocalDateTime.toEntity(
    formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
): String =
    format(formatter)

private val fullDateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun LocalDateTime.formatFullDateTime(): String =
    format(fullDateTimeFormatter)