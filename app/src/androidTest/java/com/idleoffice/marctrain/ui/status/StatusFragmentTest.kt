/*
 * Copyright (c) 2018 IdleOffice Inc.
 *
 * StatusFragmentTest.kt is part of MarcTrainTracker.
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


package com.idleoffice.marctrain.ui.status

import android.content.Context
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.data.tools.TrainLineTools
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.testsupport.KoinActivityTestRule
import com.idleoffice.marctrain.testsupport.RecyclerViewAssertions.isEmpty
import com.idleoffice.marctrain.testsupport.RecyclerViewMatcher
import com.idleoffice.marctrain.testsupport.TestIdlingResource
import com.idleoffice.marctrain.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.hamcrest.Matchers.anything
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module


@RunWith(AndroidJUnit4::class)
class StatusFragmentTest {

    private val idlingResource = TestIdlingResource()

    private val trainDataService = TestStatusTrainDataService()

    private val dataServiceModule = module {
        single(override = true) { trainDataService as TrainDataService }
    }

    @get:Rule
    val activityRule = KoinActivityTestRule(
            MainActivity::class.java,
            launchActivity = false,
            koinModules = listOf(idlingResource.idlingModule, dataServiceModule)
    )

    @Before
    fun setupTest() {
        clearSharedPrefs()
        idlingResource.idlingResource = CountingIdlingResource("status")
        IdlingRegistry.getInstance().register(idlingResource.idlingResource)
    }

    @After
    fun cleanupTest() {
        IdlingRegistry.getInstance().unregister(idlingResource.idlingResource)
        trainDataService.trainStatus.clear()
    }

    @Test
    fun testDoNothingIsEmpty() {
        activityRule.launchActivity(null)

        onView(withId(R.id.trainStatusList))
                .check(isEmpty())
    }

    @Test
    fun singleTrainSouthboundIsDisplayed() {
        trainDataService.trainStatus.add(testTrainStatus)
        activityRule.launchActivity(null)

        onView(withId(R.id.lineSpinner))
                .check(matches(hasDescendant(withText(R.string.penn))))

        clickSpinnerItem(R.id.directionSpinner, 1, R.string.south)

        onView(RecyclerViewMatcher.withRecyclerView(R.id.trainStatusList).atPosition(0))
                .check(matches(hasDescendant(withText(testTrainStatus.number))))
                .check(matches(hasDescendant(withText(testTrainStatus.nextStation))))
    }

    @Test
    fun multipleTrainsOrderedFromDc() {
        val train0 = testTrainStatus.copy(
                number = "111",
                nextStation =  TrainLineTools.PENN_STATIONS[4],
                direction = "North"
        )
        val train1 = testTrainStatus.copy(
                number = "222",
                nextStation =  TrainLineTools.PENN_STATIONS[2],
                direction = "North"
        )
        val train2 = testTrainStatus.copy(
                number = "333",
                nextStation =  TrainLineTools.PENN_STATIONS[6],
                direction = "North"
        )

        trainDataService.trainStatus.addAll(listOf(train0, train1, train2))
        activityRule.launchActivity(null)

        onView(RecyclerViewMatcher.withRecyclerView(R.id.trainStatusList).atPosition(0))
                .check(matches(hasDescendant(withText(train1.number))))

        onView(RecyclerViewMatcher.withRecyclerView(R.id.trainStatusList).atPosition(1))
                .check(matches(hasDescendant(withText(train0.number))))

        onView(RecyclerViewMatcher.withRecyclerView(R.id.trainStatusList).atPosition(2))
                .check(matches(hasDescendant(withText(train2.number))))
    }

    @Test
    fun multipleTrainsOrderedToDc() {
        val train0 = testTrainStatus.copy(
                number = "111",
                nextStation =  TrainLineTools.PENN_STATIONS[4],
                direction = "South"
        )
        val train1 = testTrainStatus.copy(
                number = "222",
                nextStation =  TrainLineTools.PENN_STATIONS[2],
                direction = "South"
        )
        val train2 = testTrainStatus.copy(
                number = "333",
                nextStation =  TrainLineTools.PENN_STATIONS[6],
                direction = "South"
        )

        trainDataService.trainStatus.addAll(listOf(train0, train1, train2))
        activityRule.launchActivity(null)

        // Navigate to Penn South view
        onView(withId(R.id.lineSpinner))
                .check(matches(hasDescendant(withText(R.string.penn))))
        clickSpinnerItem(R.id.directionSpinner, 1, R.string.south)

        onView(RecyclerViewMatcher.withRecyclerView(R.id.trainStatusList).atPosition(0))
                .check(matches(hasDescendant(withText(train2.number))))

        onView(RecyclerViewMatcher.withRecyclerView(R.id.trainStatusList).atPosition(1))
                .check(matches(hasDescendant(withText(train0.number))))

        onView(RecyclerViewMatcher.withRecyclerView(R.id.trainStatusList).atPosition(2))
                .check(matches(hasDescendant(withText(train1.number))))
    }

    @Test
    fun multipleTrainsOrderedFromDcBrunswick() {
        val train0 = testTrainStatus.copy(
                number = "111",
                line = "Brunswick",
                nextStation =  TrainLineTools.BRUNSWICK_STATIONS[4],
                direction = "West"
        )
        val train1 = testTrainStatus.copy(
                number = "222",
                line = "Brunswick",
                nextStation =  TrainLineTools.BRUNSWICK_STATIONS[2],
                direction = "West"
        )
        val train2 = testTrainStatus.copy(
                number = "333",
                line = "Brunswick",
                nextStation =  TrainLineTools.BRUNSWICK_STATIONS[6],
                direction = "West"
        )

        trainDataService.trainStatus.addAll(listOf(train0, train1, train2))
        activityRule.launchActivity(null)

        clickSpinnerItem(R.id.lineSpinner, 2, R.string.brunswick)

        onView(RecyclerViewMatcher.withRecyclerView(R.id.trainStatusList).atPosition(0))
                .check(matches(hasDescendant(withText(train1.number))))

        onView(RecyclerViewMatcher.withRecyclerView(R.id.trainStatusList).atPosition(1))
                .check(matches(hasDescendant(withText(train0.number))))

        onView(RecyclerViewMatcher.withRecyclerView(R.id.trainStatusList).atPosition(2))
                .check(matches(hasDescendant(withText(train2.number))))
    }

    @Test
    fun multipleTrainsOrderedToDcBrunswick() {
        val train0 = testTrainStatus.copy(
                number = "111",
                line = "Brunswick",
                nextStation =  TrainLineTools.BRUNSWICK_STATIONS[4],
                direction = "East"
        )
        val train1 = testTrainStatus.copy(
                number = "222",
                line = "Brunswick",
                nextStation =  TrainLineTools.BRUNSWICK_STATIONS[2],
                direction = "East"
        )
        val train2 = testTrainStatus.copy(
                number = "333",
                line = "Brunswick",
                nextStation =  TrainLineTools.BRUNSWICK_STATIONS[6],
                direction = "East"
        )

        trainDataService.trainStatus.addAll(listOf(train0, train1, train2))
        activityRule.launchActivity(null)

        clickSpinnerItem(R.id.lineSpinner, 2, R.string.brunswick)

        // Navigate to Brunswick East view
        onView(withId(R.id.lineSpinner))
                .check(matches(hasDescendant(withText(R.string.brunswick))))
        clickSpinnerItem(R.id.directionSpinner, 1, R.string.east)

        onView(RecyclerViewMatcher.withRecyclerView(R.id.trainStatusList).atPosition(0))
                .check(matches(hasDescendant(withText(train2.number))))

        onView(RecyclerViewMatcher.withRecyclerView(R.id.trainStatusList).atPosition(1))
                .check(matches(hasDescendant(withText(train0.number))))

        onView(RecyclerViewMatcher.withRecyclerView(R.id.trainStatusList).atPosition(2))
                .check(matches(hasDescendant(withText(train1.number))))
    }

    @Test
    fun checkAllLinesAndDirectionsAvailable() {
        activityRule.launchActivity(null)

        // Ensure base default is to Penn North
        onView(withId(R.id.lineSpinner))
                .check(matches(withSpinnerText(R.string.penn)))
        onView(withId(R.id.directionSpinner))
                .check(matches(withSpinnerText(R.string.north)))

        // Penn South
        clickSpinnerItem(R.id.directionSpinner, 1, R.string.south)
        onView(withId(R.id.lineSpinner))
                .check(matches(withSpinnerText(R.string.penn)))
        onView(withId(R.id.directionSpinner))
                .check(matches(withSpinnerText(R.string.south)))

        // Camden South. Note the direction is retained
        clickSpinnerItem(R.id.lineSpinner, 1, R.string.camden)
        onView(withId(R.id.lineSpinner))
                .check(matches(withSpinnerText(R.string.camden)))
        onView(withId(R.id.directionSpinner))
                .check(matches(withSpinnerText(R.string.south)))

        // Camden North
        clickSpinnerItem(R.id.directionSpinner, 0, R.string.north)
        onView(withId(R.id.lineSpinner))
                .check(matches(withSpinnerText(R.string.camden)))
        onView(withId(R.id.directionSpinner))
                .check(matches(withSpinnerText(R.string.north)))

        // Brunswick West
        clickSpinnerItem(R.id.lineSpinner, 2, R.string.brunswick)
        onView(withId(R.id.lineSpinner))
                .check(matches(withSpinnerText(R.string.brunswick)))
        onView(withId(R.id.directionSpinner))
                .check(matches(withSpinnerText(R.string.west)))

        // Brunswick East
        clickSpinnerItem(R.id.directionSpinner, 1, R.string.east)
        onView(withId(R.id.lineSpinner))
                .check(matches(withSpinnerText(R.string.brunswick)))
        onView(withId(R.id.directionSpinner))
                .check(matches(withSpinnerText(R.string.east)))
    }

    @Test
    fun retainsLastLine() {
        activityRule.launchActivity(null)

        // Ensure default line
        onView(withId(R.id.lineSpinner))
                .check(matches(withSpinnerText(R.string.penn)))
        onView(withId(R.id.directionSpinner))
                .check(matches(withSpinnerText(R.string.north)))

        clickSpinnerItem(R.id.lineSpinner, 2, R.string.brunswick)

        activityRule.finishActivity()
        activityRule.launchActivity(null)

        onView(withId(R.id.lineSpinner))
                .check(matches(withSpinnerText(R.string.brunswick)))
        onView(withId(R.id.directionSpinner))
                .check(matches(withSpinnerText(R.string.west)))

    }

    private val testTrainStatus = TrainStatus(
            "435",
            "Penn",
            "South",
            "BWI Rail Station",
            "3:36 PM",
            "On Time",
            "None",
            "03:30 PM",
            ""
    )

    private fun clickSpinnerItem(@IdRes spinnerId: Int, position: Int, @StringRes text: Int) {
        // Open Spinner
        onView(withId(spinnerId))
                .perform(click())
        // Check Data
        onData(anything())
                .atPosition(position)
                .check(matches(withText(text)))
        // Click
        onData(anything())
                .atPosition(position)
                .perform(click())
    }

    private fun clearSharedPrefs() {
        InstrumentationRegistry.getInstrumentation().targetContext
                .getSharedPreferences("prefs", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit()
    }

    private class TestStatusTrainDataService: TrainDataService {

        val trainStatus: MutableList<TrainStatus> = mutableListOf()

        override fun getTrainStatus(): Deferred<List<TrainStatus>> {
            return CoroutineScope(Dispatchers.Main).async { trainStatus }
        }

        override fun getTrainAlerts(): Deferred<List<TrainAlert>> {
            return CoroutineScope(Dispatchers.Main).async { listOf<TrainAlert>() }
        }
    }
}
