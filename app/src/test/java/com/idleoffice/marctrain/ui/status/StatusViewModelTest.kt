/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * StatusViewModelTest.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain.ui.status

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.idleoffice.marctrain.BuildConfig.STATUS_POLL_INTERVAL
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.io.IOException
import java.util.concurrent.TimeUnit

class StatusViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val contextProvider = TestCoroutineContextProvider()

    val basicTrainStatus = TrainStatus(
            "0",
            "Penn",
            "South",
            "Edgewood",
            "13:05",
            "On Time",
            "5 min",
            "10:27 PM 5/5/18",
            "Test message"
    )

    val dummyTrainStatusNumber = "0"
    val dummyTrainStatusNumber2 = "-1"
    private val trainDataService = object: TrainDataService {
        var counter = 0
        var errorOccurred = false

        val dummyTrainStatus = basicTrainStatus.copy(number = dummyTrainStatusNumber)

        val dummyTrainStatus2 = basicTrainStatus.copy(number = dummyTrainStatusNumber2)

        val dummyError = IOException("Dummy")

        override fun getTrainStatus(): Deferred<List<TrainStatus>> {
            counter++

            errorOccurred = false
            return when {
                counter == 3 -> {
                    errorOccurred = true
                    CoroutineScope(contextProvider.io).async { throw dummyError }
                }
                counter == 4 -> {
                    CoroutineScope(contextProvider.io).async { listOf(dummyTrainStatus) }
                }
                counter > 4 -> {
                    CoroutineScope(contextProvider.io).async { listOf(dummyTrainStatus2) }
                }
                else -> CoroutineScope(contextProvider.io).async { listOf(dummyTrainStatus) }
            }
        }

        override fun getTrainAlerts(): Deferred<List<TrainAlert>> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    private val networkProvider = object: NetworkProvider {
        override fun isNetworkConnected(): Boolean {
            return true
        }
    }

    private lateinit var ut: StatusViewModel

    @Before
    fun setup() {
        ut = StatusViewModel(contextProvider, trainDataService, networkProvider, FalseIdle())
    }

    private fun advanceTimeAssertValues(interval: Long, expectedCounter: Int, expectedStatus: TrainStatus) {
        contextProvider.testContext.advanceTimeBy(interval, TimeUnit.MILLISECONDS)
        assertEquals("Called unexpected number of times", expectedCounter, trainDataService.counter)
        assertEquals("Expected train status $expectedStatus", ut.allTrainStatusData.value!![0], expectedStatus)
    }

    @Test
    fun `test ordered set of events from doGetTrainStatus`() {
        ut.viewInitialize()

        advanceTimeAssertValues(100, 1, trainDataService.dummyTrainStatus)

        advanceTimeAssertValues(STATUS_POLL_INTERVAL, 2, trainDataService.dummyTrainStatus)

        assertEquals("Error should have occurred in processing", false, trainDataService.errorOccurred)
        advanceTimeAssertValues(STATUS_POLL_INTERVAL, 3, trainDataService.dummyTrainStatus)
        assertEquals("Error should have occurred in processing", true, trainDataService.errorOccurred)

        advanceTimeAssertValues(STATUS_POLL_INTERVAL, 4, trainDataService.dummyTrainStatus)
        assertEquals("Error should have occurred in processing", false, trainDataService.errorOccurred)

        advanceTimeAssertValues(STATUS_POLL_INTERVAL, 5, trainDataService.dummyTrainStatus2)
    }
}
