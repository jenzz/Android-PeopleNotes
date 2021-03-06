package com.jenzz.peoplenotes.common.ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

data class ToastMessage(
    val id: ToastMessageId = ToastMessageId(UUID.randomUUID().mostSignificantBits),
    val text: TextResource,
)

@JvmInline
value class ToastMessageId(val value: Long)

class ToastMessageManager {

    private val mutex = Mutex()

    private val _messages = MutableStateFlow(emptyList<ToastMessage>())

    val message: Flow<ToastMessage?> =
        _messages
            .map { messages -> messages.firstOrNull() }
            .distinctUntilChanged()

    suspend fun emitMessage(message: ToastMessage) {
        mutex.withLock {
            _messages.emit(
                _messages.value + message
            )
        }
    }

    suspend fun clearMessage(id: ToastMessageId) {
        mutex.withLock {
            _messages.emit(
                _messages.value.filterNot { message -> message.id == id }
            )
        }
    }
}
