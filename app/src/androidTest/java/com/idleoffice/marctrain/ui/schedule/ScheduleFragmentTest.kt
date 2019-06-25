/*
 * Copyright (c) 2019 IdleOffice Inc.
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
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.idleoffice.marctrain.ui.schedule

import android.content.Intent
import androidx.core.content.FileProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.retrofit.ts.TrainScheduleService
import com.idleoffice.marctrain.testsupport.KoinActivityTestRule
import com.idleoffice.marctrain.testsupport.TestIdlingResource
import com.idleoffice.marctrain.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.ResponseBody
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import java.io.File

@RunWith(AndroidJUnit4::class)
class ScheduleFragmentTest {

    private val idlingResource = TestIdlingResource()

    private val mockResponseBody: ResponseBody = ResponseBody.create(null, "")

    private val scheduleService = object : TrainScheduleService {
        override fun getScheduleAsync(line: String): Deferred<ResponseBody> {
            return CoroutineScope(Dispatchers.Main).async { mockResponseBody }
        }
    }

    private val scheduleServiceModule = module {
        single(override = true) { scheduleService as TrainScheduleService }
    }

    @get:Rule
    val activityRule = KoinActivityTestRule(
            MainActivity::class.java,
            koinModules = listOf(idlingResource.idlingModule, scheduleServiceModule)
    )

    @Before
    fun setup() {
        idlingResource.idlingResource = CountingIdlingResource("status")
        IdlingRegistry.getInstance().register(idlingResource.idlingResource)
        navigateToSchedule()
    }

    @After
    fun cleanup() {
        IdlingRegistry.getInstance().unregister(idlingResource.idlingResource)
    }

    @Test
    fun checkButtonsDisplayed() {
        onView(withText(R.string.penn))
                .check(matches(isDisplayed()))
        onView(withText(R.string.camden))
                .check(matches(isDisplayed()))
        onView(withText(R.string.brunswick))
                .check(matches(isDisplayed()))
    }

    @Test
    fun checkPennIntent() {
        checkIntent("penn")
        onView(withText(R.string.penn)).perform(click())
    }

    @Test
    fun checkCamdenIntent() {
        checkIntent("camden")
        onView(withText(R.string.camden)).perform(click())
    }

    @Test
    fun checkBrunswickLine() {
        checkIntent("brunswick")
        onView(withText(R.string.brunswick)).perform(click())
    }

    @Test
    fun checkMdotSchedule() {
        onView(withId(R.id.btnMdotSchedule))
                .perform(click())

        onView(withText(R.string.schedule))
                .check(matches(isDisplayed()))
    }

    private fun checkIntent(line: String) {
        val destination = File(File(activityRule.activity.filesDir, "tables"), "${line}Schedule.pdf")
        val fileUri = FileProvider.getUriForFile(activityRule.activity,
                "${BuildConfig.APPLICATION_ID}.fileprovider", destination)
        intending(allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(fileUri),
                hasType(activityRule.activity.contentResolver.getType(fileUri)),
                hasFlag(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        ))
    }

    private fun navigateToSchedule() {
        onView(withId(R.id.navigation_schedule))
                .perform(click())

        onView(withId(R.id.scheduleLayout))
                .check(matches(isDisplayed()))
    }
}