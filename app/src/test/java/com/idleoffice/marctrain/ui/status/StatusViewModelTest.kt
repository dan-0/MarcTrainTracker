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
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.data.tools.FakeNetworkProvider
import com.idleoffice.marctrain.data.tools.extensions.toLiveList
import com.idleoffice.marctrain.idling.FalseIdle
import com.idleoffice.marctrain.testsupport.TestCoroutineContextProvider
import com.idleoffice.marctrain.ui.status.data.StatusViewState
import com.idleoffice.marctrain.ui.status.data.TrainLineState
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

    private val basicTrainStatus = TrainStatus(
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

    private val dummyTrainStatusNumber1 = "0"
    private val dummyTrainStatusNumber2 = "-1"

    private val fakeTrainStatus1 = basicTrainStatus.copy(number = dummyTrainStatusNumber1)
    private val fakeTrainStatus2 = basicTrainStatus.copy(number = dummyTrainStatusNumber2)

    private val trainDataService = FakeStatusTrainDataService()

    val l = mutableListOf(
        { listOf(fakeTrainStatus1) },
        { listOf(fakeTrainStatus1) },
        { listOf(fakeTrainStatus1) },
        { throw IOException("Test Exception") },
        { listOf(fakeTrainStatus2) },
        { listOf(fakeTrainStatus1) }
    )

//        object: TrainDataService {
//        var counter = 0
//        var errorOccurred = false
//
//        val dummyTrainStatus = basicTrainStatus.copy(number = dummyTrainStatusNumber)
//
//        val dummyTrainStatus2 = basicTrainStatus.copy(number = dummyTrainStatusNumber2)
//
//        val dummyError = IOException("Dummy")
//
//        override fun getTrainStatus(): Deferred<List<TrainStatus>> {
//            counter++
//
//            errorOccurred = false
//            return when {
//                counter == 3 -> {
//                    errorOccurred = true
//                    CoroutineScope(contextProvider.io).async { throw dummyError }
//                }
//                counter == 4 -> {
//                    CoroutineScope(contextProvider.io).async { listOf(dummyTrainStatus) }
//                }
//                counter > 4 -> {
//                    CoroutineScope(contextProvider.io).async { listOf(dummyTrainStatus2) }
//                }
//                else -> CoroutineScope(contextProvider.io).async { listOf(dummyTrainStatus) }
//            }
//        }
//
//        override fun getTrainAlerts(): Deferred<List<TrainAlert>> {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//    }

    private val networkProvider = FakeNetworkProvider()

    private lateinit var ut: StatusViewModel

    private lateinit var states: List<StatusViewState>

    @Before
    fun setup() {
        ut = StatusViewModel(contextProvider, trainDataService, networkProvider, FalseIdle())

        states = ut.state.toLiveList()
    }

    @Test
    fun `init state`() {
        assertEquals(StatusViewState.Init(), states[0])
    }

    @Test
    fun `test ordered set of events from doGetTrainStatus`() {
        val fakeTrains1 = listOf(fakeTrainStatus1)
        val fakeTrains2 = listOf(fakeTrainStatus2)

        trainDataService.expectedStatusActions = mutableListOf(
            { fakeTrains1 },
            { fakeTrains1 },
            { fakeTrains1 },
            { throw IOException("Test Exception") },
            { fakeTrains2 },
            { fakeTrains1 }
        )

        ut.loadTrainStatus()

        contextProvider.testContext.advanceTimeBy(STATUS_POLL_INTERVAL * 5, TimeUnit.MILLISECONDS)

        val trainLineState = TrainLineState()
        assertEquals(StatusViewState.Init(trainLineState), states[0])

        assertEquals(StatusViewState.Content(fakeTrains1, fakeTrains1, trainLineState), states[1])

        assertEquals(StatusViewState.Content(fakeTrains1, fakeTrains1, trainLineState), states[2])

        assertEquals(StatusViewState.Content(fakeTrains1, fakeTrains1, trainLineState), states[3])

        assertEquals(StatusViewState.Content(fakeTrains2, fakeTrains2, trainLineState), states[4])

        assertEquals(StatusViewState.Content(fakeTrains1, fakeTrains1, trainLineState), states[5])
    }
}
