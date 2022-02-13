package com.jenzz.peoplenotes.ext

import androidx.compose.ui.test.junit4.AndroidComposeTestRule

fun AndroidComposeTestRule<*, *>.recreateActivity() {
    runOnUiThread {
        activity.recreate()
    }
}
