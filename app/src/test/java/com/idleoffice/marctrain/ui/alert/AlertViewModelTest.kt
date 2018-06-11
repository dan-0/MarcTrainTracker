/*
 * Copyright (c) 2018 IdleOffice Inc.
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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.idleoffice.marctrain.ui.alert

import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.InstantTaskExecutorExtension
import com.idleoffice.marctrain.MainApp
import com.idleoffice.marctrain.TestSchedulerProvider
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.TimeUnit

@ExtendWith(InstantTaskExecutorExtension::class)
internal class AlertViewModelTest {

    private inner class InitializeHelper {
        private val ts = TestScheduler()
        private val scheduler = TestSchedulerProvider(ts)

        val dummyAlert1 = TrainAlert("testDescription1", "testPubDate1")
        val dummyAlert2 = TrainAlert("testDescription2", "testPubDate2")

        private val trainDataService = object: TrainDataService {
            var counter = 0
            var errorOccurred = false

            val dummyError = Exception()

            override fun getTrainStatus(): Observable<List<TrainStatus>> {
                throw IllegalArgumentException("This shouldn't be called here")
            }
            override fun getTrainAlerts(): Observable<List<TrainAlert>> {
                counter++
                return when {
                    counter == 3 -> {
                        errorOccurred = true
                        throw dummyError
                    }
                    counter >= 4 -> Observable.fromArray(listOf(dummyAlert2))
                    else -> Observable.fromArray(listOf(dummyAlert1))
                }
            }
        }

        val app: MainApp = mock()

        private val ut = AlertViewModel(app, scheduler, trainDataService)

        init {
            ut.viewInitialize()
            ts.advanceTimeBy(1, TimeUnit.SECONDS)
        }

        fun `test first and second return dummyTrainAlerts`() {
            // Make sure we get our first event value: dummyTrainStatus
            Assertions.assertEquals(dummyAlert1, ut.allAlerts.value!![0])

            // Second event: dummyTrainStatus
            ts.advanceTimeBy(BuildConfig.ALERT_POLL_INTERVAL, TimeUnit.SECONDS)
            Assertions.assertEquals(dummyAlert1, ut.allAlerts.value!![0])
        }

        fun `test error from third event`() {
            // Third event: error
            ts.advanceTimeBy(BuildConfig.ALERT_POLL_INTERVAL, TimeUnit.SECONDS)
            assertTrue(trainDataService.errorOccurred)
        }

        fun `test fourth event provides new alerts`() {
            // Fifth event: dummyAlert2
            // using retry interval to ensure error timing
            ts.advanceTimeBy(BuildConfig.ALERT_POLL_RETRY_INTERVAL, TimeUnit.SECONDS)
            Assertions.assertEquals(dummyAlert2, ut.allAlerts.value!![0])
        }

        fun `assert number of calls`(number: Int) {
            Assertions.assertEquals(number, trainDataService.counter)
        }

    }


    @Test
    fun initialize() {
        val helper = InitializeHelper()
        helper.`test first and second return dummyTrainAlerts`()
        helper.`test error from third event`()
        helper.`test fourth event provides new alerts`()
        helper.`assert number of calls`(4)
    }
}