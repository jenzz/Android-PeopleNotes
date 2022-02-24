package com.jenzz.peoplenotes.rules

import com.jenzz.peoplenotes.common.data.CoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class CoroutineTestRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {

    val testCoroutineDispatchers = object : CoroutineDispatchers {
        override val Default: CoroutineDispatcher = testDispatcher
        override val Main: CoroutineDispatcher = testDispatcher
        override val Unconfined: CoroutineDispatcher = testDispatcher
        override val IO: CoroutineDispatcher = testDispatcher
    }

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}
