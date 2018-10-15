/*
 * Copyright (c) 2018 IdleOffice Inc.
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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.idleoffice.marctrain.ui.main

import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
//@Config(constants = BuildConfig::class)
class MainActivityTest {

    private lateinit var ut: MainActivity
    private val helper = MainActivityTestHelper()

    @Before
    fun setUp() {
        StandAloneContext.loadKoinModules(helper.testModule)
        ut = Robolectric.setupActivity(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        StandAloneContext.closeKoin()
    }

    @Test
    fun `navigation buttons load fragments`() {

        val navItems = mapOf(
                Pair(R.id.navigation_status, R.id.statusLayout),
                Pair(R.id.navigation_alert, R.id.alertLayout),
                Pair(R.id.navigation_schedule, R.id.scheduleLayout)
        )

        navItems.forEach {
            ut.navigation.selectedItemId = it.key
            assertNotNull(ut.findViewById(it.value))

            navItems.filter { ni ->
                ni.key != it.key
            }.forEach { ni ->
                assertNull(ut.findViewById(ni.value))
            }
        }
    }

    private class MainActivityTestHelper {
        val testModule : Module = applicationContext {
            bean { object: TrainDataService {
                override fun getTrainStatus(): Observable<List<TrainStatus>> {
                    return Observable.fromCallable { emptyList<TrainStatus>() }
                }

                override fun getTrainAlerts(): Observable<List<TrainAlert>> {
                    return Observable.fromCallable { emptyList<TrainAlert>() }
                }

            } }
        }
    }
}