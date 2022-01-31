package com.jenzz.peoplenotes.rules

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import org.junit.rules.ExternalResource
import org.robolectric.shadows.ShadowLog

class RedirectLogcatToConsoleRule : ExternalResource() {

    override fun before() {
        ShadowLog.stream = System.out
    }
}

fun ComposeContentTestRule.printViewHierarchy(
    tag: String = "View Hierarchy",
) {
    onRoot(useUnmergedTree = true)
        .printToLog(tag)
}
