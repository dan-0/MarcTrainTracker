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

import android.content.res.AssetManager
import com.idleoffice.marctrain.RobolectricTest
import com.idleoffice.marctrain.TestContextProvider
import com.idleoffice.marctrain.coroutines.ContextProvider
import io.reactivex.schedulers.TestScheduler
import kotlinx.android.synthetic.main.fragment_schedule.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
import java.io.File

class ScheduleFragmentTest: RobolectricTest() {

    private lateinit var ut: ScheduleFragment
    private val helper = ScheduleTestHelper()


    @Before
    fun setUp() {
        loadKoinModules(helper.scheduleTestModule)
        ut = ScheduleFragment()
        SupportFragmentTestUtil.startFragment(ut)
    }

    @After
    fun tearDown() {
        closeKoin()
    }

    @Test
    fun `test line schedules selected`() {
        // Setup te objects for each schedule that can be selected
        val testObjects = arrayOf(
                Pair("pennFull.pdf", ut.btnTablesPenn),
                Pair("camdenFull.pdf", ut.btnTablesCamden),
                Pair("brunswickFull.pdf", ut.btnTablesBrunswick)
        )

        for (o in testObjects) {
            val testNavigator = ScheduleTestHelper.TestScheduleNavigator(ut.appFilesDir, ut.appAssets)
            ut.fragViewModel.navigator = testNavigator
            o.second.performClick()
            helper.ts.triggerActions()

            Assert.assertEquals(1, testNavigator.startPdfCalledTimes)
            Assert.assertEquals(o.first, testNavigator.lastPdfFilePath)
        }

    }

    private class ScheduleTestHelper {
        val ts = TestScheduler()
        private val scheduler = TestContextProvider(ts)

        val scheduleTestModule = applicationContext {
            bean { scheduler as ContextProvider }
        }

        class TestScheduleNavigator(override var appFilesDir: File?, override var appAssets: AssetManager?): ScheduleNavigator {
            override fun vibrateTap() {}

            var startPdfCalledTimes = 0
            var lastPdfFilePath = ""

            override fun startPdfActivity(destination: File) {
                startPdfCalledTimes++
                lastPdfFilePath = destination.name
            }
        }
    }
}