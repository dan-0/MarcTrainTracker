package com.idleoffice.marctrain.ui.status

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.res.Resources
import com.idleoffice.marctrain.*
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.nhaarman.mockito_kotlin.stub
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeUnit

internal class StatusViewModelTest {

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockApp : MainApp

    val scheduler = TrampolineSchedulerProvider()

    var trainDataService = object: TrainDataService {
        override fun getTrainStatus(): Observable<List<TrainStatus>> {
            return Observable.fromArray(listOf())
        }

        override fun getTrainAlerts(): Observable<List<TrainAlert>> {
            return Observable.fromArray(listOf())
        }

    }

    @Mock
    private lateinit var mockResources: Resources


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        whenever(mockApp.resources).then { mockResources }
    }


    @After
    fun tearDown() {

    }

    @Test
    fun initialize() {
        val ut = StatusViewModel(mockApp, scheduler, trainDataService)
        ut.viewInitialize()

        assertEquals(1, ut.compositeDisposable.size())
    }

    @Test
    fun doGetTrainStatus() {
        val ts = TestScheduler()
        val scheduler = TestSchedulerProvider(ts)
        val mockTrainDataService = mock(TrainDataService::class.java)

        val ut = StatusViewModel(mockApp, scheduler, mockTrainDataService)

        val observableinterval = Observable.interval(0,
                BuildConfig.STATUS_POLL_INTERVAL,
                TimeUnit.SECONDS,
                ts)
        ut.doGetTrainStatus(observableinterval)

        val dummyTrainStatusNumber = "-0"
        val dummyTrainStatus = mock(TrainStatus::class.java)
        whenever(dummyTrainStatus.number).thenReturn(dummyTrainStatusNumber)

        val dummyTrainStatusNumber2 = "-1"
        val dummyTrainStatus2 = mock(TrainStatus::class.java)
        whenever(dummyTrainStatus2.number).thenReturn(dummyTrainStatusNumber2)

        val dummyError = Exception("Dummy")

        whenever(mockTrainDataService.getTrainStatus())
                .then {
                    Observable.fromArray(listOf(dummyTrainStatus))
                }
                .then {
                    Observable.fromArray(listOf(dummyTrainStatus))
                }
                .then {
                    Observable.error<Exception>(dummyError)
                }
                .then {
                    Observable.fromArray(listOf(dummyTrainStatus2))
                }

        ut.doGetTrainStatus(observableinterval)

        ts.advanceTimeBy(1, TimeUnit.SECONDS)

        assertEquals(dummyTrainStatusNumber, ut.allTrainStatusData.value!![0])
    }

    @Test
    fun trainLineSelected() {
    }

    @Test
    fun trainDirectionSelected() {
    }
}