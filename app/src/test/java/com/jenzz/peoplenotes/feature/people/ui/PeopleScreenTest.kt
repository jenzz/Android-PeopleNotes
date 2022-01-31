package com.jenzz.peoplenotes.feature.people.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import com.jenzz.peoplenotes.MainActivity
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.rules.CoroutineTestRule
import com.jenzz.peoplenotes.rules.RedirectLogcatToConsoleRule
import com.jenzz.peoplenotes.rules.printViewHierarchy
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@Ignore
@RunWith(RobolectricTestRunner::class)
class PeopleScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @get:Rule
    val redirectLogcatToConsoleRule = RedirectLogcatToConsoleRule()

    @Test
    fun `saves and restores list layout across configuration changes`() {
        ActivityScenario
            .launch(MainActivity::class.java)
            .use { scenario ->
                scenario.onActivity { activity ->
                    composeTestRule.printViewHierarchy()

                    composeTestRule
                        .onNodeWithContentDescription(
                            activity.getString(R.string.grid_view), useUnmergedTree = true
                        )
                        .assertIsDisplayed()
                        .performClick()

                    composeTestRule
                        .onNodeWithContentDescription(activity.getString(R.string.rows))
                        .assertIsDisplayed()

                    activity.recreate()

                    composeTestRule
                        .onNodeWithContentDescription(activity.getString(R.string.rows))
                        .assertIsDisplayed()
                }
            }
    }
}
