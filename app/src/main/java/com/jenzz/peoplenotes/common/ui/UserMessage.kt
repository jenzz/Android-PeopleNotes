package com.jenzz.peoplenotes.common.ui

import java.util.*

data class UserMessage(
    val id: UserMessageId = UserMessageId(UUID.randomUUID().mostSignificantBits),
    val text: TextResource,
)

@JvmInline
value class UserMessageId(val value: Long)