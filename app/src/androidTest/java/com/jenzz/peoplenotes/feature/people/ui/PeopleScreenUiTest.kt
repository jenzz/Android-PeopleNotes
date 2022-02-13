package com.jenzz.peoplenotes.feature.people.ui

import androidx.compose.ui.test.IdlingResource
import com.jenzz.peoplenotes.common.data.CoroutineDispatchers
import com.jenzz.peoplenotes.common.data.IdlingDispatcher
import com.jenzz.peoplenotes.common.data.di.CoroutineModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

@UninstallModules(CoroutineModule::class)
@HiltAndroidTest
class PeopleScreenUiTest : PeopleScreenTest() {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var coroutineDispatchers: CoroutineDispatchers

    @Before
    fun setUp() {
        hiltRule.inject()
        composeTestRule.registerIdlingResource(coroutineDispatchers.Default as IdlingResource)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object TestCoroutineModule {

    @Provides
    fun provideCoroutineDispatchers(): CoroutineDispatchers {
        return object : CoroutineDispatchers {

            override val Default: CoroutineDispatcher =
                IdlingDispatcher(Dispatchers.Default)

            override val Main: CoroutineDispatcher =
                IdlingDispatcher(Dispatchers.Main)

            override val Unconfined: CoroutineDispatcher =
                IdlingDispatcher(Dispatchers.Unconfined)

            override val IO: CoroutineDispatcher =
                IdlingDispatcher(Dispatchers.IO)
        }
    }
}
