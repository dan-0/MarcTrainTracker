/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * MainActivityTest.kt is part of MarcTrainTracker.
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
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.idleoffice.marctrain.ui.main

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idleoffice.marctrain.R.id
import com.idleoffice.marctrain.testsupport.KoinActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = KoinActivityTestRule(MainActivity::class.java)

    @Test
    fun testButtonsLoadFragments() {
        // Alerts
        onView(withId(id.navigation_alert))
                .perform(click())

        onView(withId(id.alertLayout))
                .check(matches(isDisplayed()))

        // Schedule
        onView(withId(id.navigation_schedule))
                .perform(click())

        onView(withId(id.scheduleLayout))
                .check(matches(isDisplayed()))

        // Back to status
        onView(withId(id.navigation_status))
                .perform(click())

        onView(withId(id.statusLayout))
                .check(matches(isDisplayed()))
    }
}