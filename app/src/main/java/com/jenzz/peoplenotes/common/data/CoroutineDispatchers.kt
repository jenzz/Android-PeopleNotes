package com.jenzz.peoplenotes.common.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@Suppress("PropertyName")
interface CoroutineDispatchers {

    val Default: CoroutineDispatcher

    val Main: CoroutineDispatcher

    val Unconfined: CoroutineDispatcher

    val IO: CoroutineDispatcher
}

class Dispatchers @Inject constructor() : CoroutineDispatchers {

    override val Default: CoroutineDispatcher = Dispatchers.Default

    override val Main: CoroutineDispatcher = Dispatchers.Main

    override val Unconfined: CoroutineDispatcher = Dispatchers.Unconfined

    override val IO: CoroutineDispatcher = Dispatchers.IO
}