/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * AlertViewModelTest.kt is part of MarcTrainTracker.
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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.idling.FalseIdle
import com.idleoffice.marctrain.network.NetworkProvider
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.testsupport.TestCoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.io.IOException
import java.util.concurrent.TimeUnit

class AlertViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val coroutineContextProvider = TestCoroutineContextProvider()

    val dummyAlert1 = TrainAlert("testDescription1", "testPubDate1")
    val dummyAlert2 = TrainAlert("testDescription2", "testPubDate2")

    private val trainDataService = object: TrainDataService {
        var counter = 0
        var errorOccurred = false

        val dummyError = IOException()

        override fun getTrainStatus(): Deferred<List<TrainStatus>> {
            throw IllegalArgumentException("This shouldn't be called here")
        }
        override fun getTrainAlerts(): Deferred<List<TrainAlert>> {
            counter++
            return when {
                counter == 3 -> {
                    errorOccurred = true
                    CoroutineScope(coroutineContextProvider.io).async { throw dummyError }
                }
                counter >= 4 -> CoroutineScope(coroutineContextProvider.io).async { listOf(dummyAlert2) }
                else ->  CoroutineScope(coroutineContextProvider.io).async { listOf(dummyAlert1) }
            }
        }
    }
    
    private var networkService = object : NetworkProvider {
        override fun isNetworkConnected(): Boolean {
            return true
        }
    }

    private val ut = AlertViewModel(coroutineContextProvider, trainDataService, networkService, FalseIdle())
    
    @Before
    fun setup() {
        ut.viewInitialize()
    }

    @Test
    fun `test first and second return dummyTrainAlerts`() {
        coroutineContextProvider.testContext.advanceTimeBy(BuildConfig.ALERT_POLL_INTERVAL, TimeUnit.MILLISECONDS)

        // Make sure we get our first event value: dummyTrainStatus
        assertEquals(dummyAlert1, ut.allAlerts.value!![0])

        // Second event: dummyTrainStatus
        coroutineContextProvider.testContext.advanceTimeBy(BuildConfig.ALERT_POLL_INTERVAL, TimeUnit.MILLISECONDS)
        assertEquals(dummyAlert1, ut.allAlerts.value!![0])
    }

    @Test
    fun `test error from third event`() {
        // Third event: error
        coroutineContextProvider.testContext.advanceTimeBy(BuildConfig.ALERT_POLL_INTERVAL * 2, TimeUnit.MILLISECONDS)
        assertTrue(trainDataService.errorOccurred)
    }

    @Test
    fun `test fourth event provides new alerts`() {
        // Fifth event: dummyAlert2
        // using retry interval to ensure error timing
        coroutineContextProvider.testContext.advanceTimeBy(BuildConfig.ALERT_POLL_INTERVAL * 3, TimeUnit.MILLISECONDS)
        assertEquals(dummyAlert2, ut.allAlerts.value!![0])
        assertEquals(4, trainDataService.counter)
    }

    @Test
    fun `assert number of calls`() {
        val numCalls = 3

        coroutineContextProvider.testContext.advanceTimeBy(BuildConfig.ALERT_POLL_INTERVAL * (numCalls - 1), TimeUnit.MILLISECONDS)
        assertEquals(numCalls, trainDataService.counter)
    }
}