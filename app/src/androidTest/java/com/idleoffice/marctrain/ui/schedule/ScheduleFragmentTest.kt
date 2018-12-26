/*
 * Copyright (c) 2018 IdleOffice Inc.
 *
 * ScheduleFragmentTest.kt is part of MarcTrainTracker.
 *
 * MarcTrainTracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MarcTrainTracker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.idleoffice.marctrain.ui.schedule

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.testsupport.KoinActivityTestRule
import com.idleoffice.marctrain.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_schedule.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScheduleFragmentTest {

    @get:Rule
    val activityRule = KoinActivityTestRule(
            MainActivity::class.java
    )

    @Before
    fun setup() {
    }

    @Test
    fun testWebViewOpens() {
        navigateToSchedule()
    }

    @Test
    fun checkInitialUrl() {
        navigateToSchedule()

        var loadedUrl: String? = null

        activityRule.runOnUiThread {
            loadedUrl = activityRule.activity.scheduleWebView.url

        }
        assertEquals(ScheduleFragment.MARC_SCHEDULE_URL, loadedUrl)
    }

    private fun navigateToSchedule() {
        onView(withId(R.id.navigation_schedule))
                .perform(click())

        onView(withId(R.id.scheduleLayout))
                .check(matches(isDisplayed()))
    }
}