package com.jenzz.peoplenotes.rules

import org.junit.rules.ExternalResource
import org.robolectric.shadows.ShadowLog

class RedirectLogcatToConsoleRule : ExternalResource() {

    override fun before() {
        ShadowLog.stream = System.out
    }
}
