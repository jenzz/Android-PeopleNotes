package kotlinx.coroutines.test

import com.jenzz.peoplenotes.common.data.CoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher

class TestCoroutineDispatchers(
    private val scope: TestScope = TestScope(),
) : CoroutineDispatchers {

    private val dispatcher: TestDispatcher = StandardTestDispatcher(scope.testScheduler)

    override val Default: CoroutineDispatcher = dispatcher

    override val Main: CoroutineDispatcher = dispatcher

    override val Unconfined: CoroutineDispatcher = dispatcher

    override val IO: CoroutineDispatcher = dispatcher

    fun runTest(testBody: suspend TestScope.() -> Unit) {
        scope.runTest(testBody = testBody)
    }
}
