package com.jenzz.peoplenotes.common.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope

class TestCoroutineDispatchers(
    scope: TestScope = TestScope(),
) : CoroutineDispatchers {

    private val dispatcher: TestDispatcher = StandardTestDispatcher(scope.testScheduler)

    override val Default: CoroutineDispatcher = dispatcher

    override val Main: CoroutineDispatcher = dispatcher

    override val Unconfined: CoroutineDispatcher = dispatcher

    override val IO: CoroutineDispatcher = dispatcher
}
