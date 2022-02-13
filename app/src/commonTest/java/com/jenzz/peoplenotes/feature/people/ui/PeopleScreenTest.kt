package com.jenzz.peoplenotes.feature.people.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.jenzz.peoplenotes.MainActivity
import com.jenzz.peoplenotes.R
import com.jenzz.peoplenotes.ext.recreateActivity
import org.junit.Rule
import org.junit.Test

abstract class PeopleScreenTest {

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

//    @Before
//    fun setUp() {
//        composeTestRule.setContent {
//            PeopleScreen(
//                navigator = EmptyDestinationsNavigator,
//                deletePersonResultRecipient = EmptyResultRecipient(),
//                deletePersonWithNotesResultRecipient = EmptyResultRecipient(),
//            )
//        }
//    }

    @Test
    fun savesAndRestoresListLayoutAcrossConfigurationChanges() {
//        onView(withContentDescription(R.string.grid_view))
//            .check(matches(isDisplayed()))
//            .perform(click())
        composeTestRule
            .onNodeWithContentDescription(composeTestRule.activity.getString(R.string.grid_view))
            .assertIsDisplayed()
            .performClick()

//        onView(withContentDescription(R.string.rows))
//            .check(matches(isDisplayed()))
        composeTestRule
            .onNodeWithContentDescription(composeTestRule.activity.getString(R.string.rows))
            .assertIsDisplayed()

        composeTestRule.recreateActivity()

//        onView(withContentDescription(R.string.rows))
//            .check(matches(isDisplayed()))
        composeTestRule
            .onNodeWithContentDescription(composeTestRule.activity.getString(R.string.rows))
            .assertIsDisplayed()
    }
}
