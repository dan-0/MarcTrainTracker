/*
 * Copyright (c) 2018 IdleOffice Inc.
 *
 * TestHelpers.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain

import android.arch.core.executor.ArchTaskExecutor
import android.arch.core.executor.TaskExecutor
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.rx.SchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

class TrampolineSchedulerProvider : SchedulerProvider {
    override fun ui(): Scheduler {return Schedulers.trampoline()}
    override fun io(): Scheduler {return Schedulers.trampoline()}
}

class TestSchedulerProvider(private val ts: TestScheduler) : SchedulerProvider {
    override fun ui(): Scheduler { return ts }
    override fun io(): Scheduler { return ts }
}

/**
 * Helper to simulate a "any()" type parameter for testing
 */
fun <T> any(): T {
    Mockito.any<T>()
    // Intentionally allowing this to pretend to be of type 'T'
    @Suppress("UNCHECKED_CAST")
    return null as T
}

/**
 * Builder to simplify creating TrainStatuses
 */
class DummyTrainStatusBuilder {
    private var number = "0"
    private var line = "Penn"
    private var direction = "South"
    private var nextStation = "Edgewood"
    private var departure = "13:05"
    private var status = "On Time"
    private var delay = "5 min"
    private var lastUpdate = "10:27 PM 5/5/18"
    private var message = "Test message"

    fun number(value: String): DummyTrainStatusBuilder {
        number = value
        return this
    }

    fun line(value: String): DummyTrainStatusBuilder {
        line = value
        return this
    }

    fun direction(value: String): DummyTrainStatusBuilder {
        direction = value
        return this
    }

    fun nextStation(value: String): DummyTrainStatusBuilder {
        nextStation = value
        return this
    }

    fun departure(value: String): DummyTrainStatusBuilder {
        departure = value
        return this
    }

    fun status(value: String): DummyTrainStatusBuilder {
        status = value
        return this
    }

    fun delay(value: String): DummyTrainStatusBuilder {
        delay = value
        return this
    }

    fun message(value: String): DummyTrainStatusBuilder {
        message = value
        return this
    }

    fun build(): TrainStatus {
        return TrainStatus(
                number,
                line,
                direction,
                nextStation,
                departure,
                status,
                delay,
                lastUpdate,
                message
        )
    }


}

/**
 * Custom extension to replace the functionality of InstantTaskExecutorRule from JUnit 4
 */
class InstantTaskExecutorExtension: BeforeEachCallback, AfterEachCallback {

    override fun beforeEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean {
                return true
            }
        })
    }

    override fun afterEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }
}

/**
 * Abstract class to simplify generation of Robolectric tests.
 * Note: Robolectric uses JUnit 4.
  */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
abstract class RobolectricTest: AutoCloseKoinTest()