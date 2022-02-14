package com.jenzz.peoplenotes.common.data

import androidx.compose.ui.test.IdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import java.util.*
import kotlin.coroutines.CoroutineContext

class IdlingDispatcher(
    private val parent: CoroutineDispatcher,
) : CoroutineDispatcher(), IdlingResource {

    private val jobs = Collections.newSetFromMap(WeakHashMap<Job, Boolean>())

    override val isIdleNow: Boolean
        get() = synchronized(jobs) {
            jobs.removeAll { !it.isActive }
            jobs.isEmpty()
        }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        context[Job]?.let { addNewJob(it) }
        parent.dispatch(context, block)
    }

    @InternalCoroutinesApi
    override fun dispatchYield(context: CoroutineContext, block: Runnable) {
        context[Job]?.let { addNewJob(it) }
        parent.dispatchYield(context, block)
    }

    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        context[Job]?.let { addNewJob(it) }
        return parent.isDispatchNeeded(context)
    }

    private fun addNewJob(job: Job) {
        synchronized(jobs) {
            jobs.add(job)
        }
    }
}
