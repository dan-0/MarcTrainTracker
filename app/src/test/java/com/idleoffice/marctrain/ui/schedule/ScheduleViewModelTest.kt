/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * ScheduleViewModelTest.kt is part of MarcTrainTracker.
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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.idleoffice.marctrain.data.tools.extensions.toLiveList
import com.idleoffice.marctrain.idling.FalseIdle
import com.idleoffice.marctrain.retrofit.ts.TrainScheduleService
import com.idleoffice.marctrain.testsupport.TestCoroutineContextProvider
import com.idleoffice.marctrain.ui.schedule.interactor.ScheduleAction
import com.idleoffice.marctrain.ui.schedule.interactor.ScheduleEvent
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import okhttp3.ResponseBody
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.rules.TestRule

class ScheduleViewModelTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val mockFile = run {
        tempFolder.create()
        tempFolder.newFolder()
    }

    private lateinit var mockTrainScheduleService: TrainScheduleService

    private lateinit var ut: ScheduleViewModel

    private val coroutineContext = TestCoroutineContextProvider()

    private lateinit var events: List<ScheduleEvent>

    @Before
    fun setup() {

        mockTrainScheduleService = mock()

        val fakeAnalyticsService = FakeAnalyticsService()

        ut = ScheduleViewModel(
            dispatchers = coroutineContext,
            idlingResource = FalseIdle(),
            trainScheduleService = mockTrainScheduleService,
            appFileDir = mockFile,
            analyticService = fakeAnalyticsService
        )

        events = ut.event.toLiveList()
    }

    @Test
    fun `test Penn line file download`() {
        testfileDownload("penn")
    }

    @Test
    fun `test Camden line file download`() {
        testfileDownload("camden")
    }

    @Test
    fun `test Brunswick line file download`() {
        testfileDownload("brunswick")
    }

    @Test
    fun `test action error occurs`() {
        ut.takeAction(ScheduleAction.LaunchPenn)

        coroutineContext.testContext.triggerActions()

        // Should be NPE because we never supplied a ResponseBody
        assertTrue((events.last() as ScheduleEvent.Error).e is NullPointerException)
    }

    private fun testfileDownload(line: String) {
        val mockResponseBody: ResponseBody = mock()

        whenever(mockResponseBody.byteStream()).thenReturn("".byteInputStream())

        whenever(mockTrainScheduleService.getScheduleAsync(line)).thenReturn(
            CoroutineScope(coroutineContext.io).async { mockResponseBody }
        )

        val action = when (line) {
            "penn" -> ScheduleAction.LaunchPenn
            "camden" -> ScheduleAction.LaunchCamden
            "brunswick" -> ScheduleAction.LaunchBrunswick
            else -> {
                fail("Invalid line: $line")
                null // Compiler forces this because it doesn't see the exception from `fail`
            }
        }

        ut.takeAction(action!!)

        coroutineContext.testContext.triggerActions()

        assertTrue((events.last() as ScheduleEvent.Data).file.name.endsWith("${line}Schedule.pdf"))
    }
}

