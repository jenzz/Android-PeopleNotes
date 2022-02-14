package com.jenzz.peoplenotes.feature.people.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PeopleScreenJvmTest : PeopleScreenTest() {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
}
