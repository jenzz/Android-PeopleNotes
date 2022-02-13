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

class PeopleScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun savesAndRestoresListLayoutAcrossConfigurationChanges() {
        composeTestRule
            .onNodeWithContentDescription(composeTestRule.activity.getString(R.string.grid_view))
            .assertIsDisplayed()
            .performClick()

        composeTestRule
            .onNodeWithContentDescription(composeTestRule.activity.getString(R.string.rows))
            .assertIsDisplayed()

        composeTestRule.recreateActivity()

        composeTestRule
            .onNodeWithContentDescription(composeTestRule.activity.getString(R.string.rows))
            .assertIsDisplayed()
    }
}
