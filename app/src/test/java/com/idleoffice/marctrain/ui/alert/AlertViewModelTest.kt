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
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.data.tools.FakeNetworkProvider
import com.idleoffice.marctrain.data.tools.extensions.toLiveList
import com.idleoffice.marctrain.idling.FalseIdle
import com.idleoffice.marctrain.testsupport.TestCoroutineContextProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.threeten.bp.Duration
import java.io.IOException
import java.util.concurrent.TimeUnit

class AlertViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val coroutineContextProvider = TestCoroutineContextProvider()

    private val fakeAlert1 = TrainAlert("testDescription1", "testPubDate1")
    private val fakeAlert2 = TrainAlert("testDescription2", "testPubDate2")

    private val trainDataService = FakeAlertTrainDataService()
    
    private val networkService = FakeNetworkProvider()

    private val ut = AlertViewModel(coroutineContextProvider, trainDataService, networkService, FalseIdle())

    private lateinit var states: List<AlertViewState>

    @Before
    fun setUp() {
        states = ut.state.toLiveList()
    }

    @Test
    fun `init state`() {
        assertEquals(states[0], AlertViewState.Init)
    }

    @Test
    fun `loadAlerts polling behavior`() {
        trainDataService.expectedAlertActions = mutableListOf(
            { listOf(fakeAlert1) },
            { listOf(fakeAlert2) }
        )

        ut.loadAlerts()

        coroutineContextProvider.testContext.advanceTimeBy(ALERT_POLL_INTERVAL.toMillis(), TimeUnit.MILLISECONDS)
        coroutineContextProvider.testContext.advanceTimeBy(ALERT_POLL_INTERVAL.toMillis(), TimeUnit.MILLISECONDS)

        val firstState = states[1] as AlertViewState.Content
        val secondState = states[2] as AlertViewState.Content

        assertEquals(fakeAlert1, firstState.alerts[0])
        assertEquals(fakeAlert2, secondState.alerts[0])
    }

    @Test
    fun `error caused in polling is recoverable`() {
        trainDataService.expectedAlertActions = mutableListOf(
            { throw IOException("Test exception") },
            { listOf(fakeAlert2) }
        )

        ut.loadAlerts()

        coroutineContextProvider.testContext.advanceTimeBy(ALERT_POLL_INTERVAL.toMillis(), TimeUnit.MILLISECONDS)
        coroutineContextProvider.testContext.advanceTimeBy(ALERT_POLL_INTERVAL.toMillis(), TimeUnit.MILLISECONDS)

        assertTrue(states[1] is AlertViewState.Error)
        assertTrue(states[2] is AlertViewState.Content)
    }

    companion object {
        private val ALERT_POLL_INTERVAL = Duration.ofMinutes(1)
    }
}

