/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * AlertFragmentTest.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain.ui.alert

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idleoffice.marctrain.R.id
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.testsupport.KoinActivityTestRule
import com.idleoffice.marctrain.testsupport.RecyclerViewMatcher.Companion.withRecyclerView
import com.idleoffice.marctrain.testsupport.TestIdlingResource
import com.idleoffice.marctrain.ui.main.MainActivity
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules


@RunWith(AndroidJUnit4::class)
class AlertFragmentTest {

    private var idlingResource: TestIdlingResource = TestIdlingResource()

    private val dataServiceModule = module {
        single(override = true) { TestAlertTrainDataService(listOf(dummyAlert, dummyAlert2)) as TrainDataService }
    }

    @get:Rule
    val activityRule = KoinActivityTestRule(
            MainActivity::class.java,
            launchActivity = false,
            koinModules = listOf(idlingResource.idlingModule, dataServiceModule)
    )

    @Before
    fun setup() {
        idlingResource.idlingResource = CountingIdlingResource("test")
        IdlingRegistry.getInstance().register(idlingResource.idlingResource)
        activityRule.launchActivity(null)
    }

    @After
    fun cleanup() {
        IdlingRegistry.getInstance().unregister(idlingResource.idlingResource)
    }

    @Test
    fun ensureAlertsDisplayed() {
        onView(withId(id.navigation_alert))
            .perform(click())

        onView(withId(id.loading_layout))
                .check(matches(not(isDisplayed())))

        onView(withRecyclerView(id.trainAlertList).atPosition(0))
                .check(matches(hasDescendant(withText(dummyAlert.description))))

        onView(withRecyclerView(id.trainAlertList).atPosition(1))
                .check(matches(hasDescendant(withText(dummyAlert2.description))))
    }

    @Test
    fun noResultsFoundGraphic() {

        // change handler
        val noAlertsModule = module {
            single(override = true) { TestAlertTrainDataService(listOf()) as TrainDataService }
        }

        activityRule.runOnUiThread { loadKoinModules(noAlertsModule) }

        onView(withId(id.navigation_alert))
                .perform(click())

        onView(withId(id.loading_layout))
                .check(matches(isDisplayed()))
    }

    private val dummyAlert = TrainAlert(
            "Dummy Alert",
            "Sun, 27 May 2018 15:50:43 GMT"
    )

    private val dummyAlert2 = TrainAlert(
            "Dummy Alert",
            "Sun, 27 May 2018 15:50:43 GMT"
    )

    private class TestAlertTrainDataService(
            private val dummyAlerts: List<TrainAlert> = listOf()
    ): TrainDataService {
        override suspend fun getTrainStatus(): List<TrainStatus> = listOf()

        override suspend fun getTrainAlerts(): List<TrainAlert> = dummyAlerts
    }
}