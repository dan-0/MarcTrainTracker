package com.idleoffice.marctrain.ui.status

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.res.Resources
import com.idleoffice.marctrain.MainApp
import com.idleoffice.marctrain.TrampolineSchedulerProvider
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.*

import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

internal class StatusViewModelTest {

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    lateinit var ut: StatusViewModel

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
        ut = spy(StatusViewModel(mockApp, scheduler, trainDataService))

        doNothing().whenever(ut).doGetTrainStatus(Observable.fromArray(arrayListOf()))

        ut.viewInitialize()
    }

    @Test
    fun trainLineSelected() {
    }

    @Test
    fun trainDirectionSelected() {
    }
}