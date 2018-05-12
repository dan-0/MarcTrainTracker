package com.idleoffice.marctrain

import android.app.Application
import android.content.res.AssetManager
import android.content.res.Resources
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.di.appModules
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.idleoffice.marctrain.ui.alert.AlertAdapter
import com.idleoffice.marctrain.ui.alert.AlertViewModel
import com.idleoffice.marctrain.ui.alert.alertFragmentModule
import com.idleoffice.marctrain.ui.main.MainViewModel
import com.idleoffice.marctrain.ui.main.mainActivityModule
import com.idleoffice.marctrain.ui.schedule.ScheduleNavigator
import com.idleoffice.marctrain.ui.schedule.ScheduleViewModel
import com.idleoffice.marctrain.ui.schedule.scheduleFragmentModule
import com.idleoffice.marctrain.ui.status.StatusAdapter
import com.idleoffice.marctrain.ui.status.StatusViewModel
import com.idleoffice.marctrain.ui.status.statusFragmentModule
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import java.io.File

/**
 * Tests to ensure dependencies are injected. Does not test dependency functionality.
 */
@ExtendWith(InstantTaskExecutorExtension::class)
internal class DiTest: KoinTest {

    private val mockApp : Application = mock()
    private val mockResources : Resources = mock()

    init {
        whenever(mockApp.resources).thenReturn(mockResources)
    }

    private val testAppModule : Module = applicationContext { bean {mockApp} }

    private val koinModules = listOf(
            appModules,
            scheduleFragmentModule,
            mainActivityModule,
            statusFragmentModule,
            alertFragmentModule,
            testAppModule
            )

    private val scheduleViewModel : ScheduleViewModel by inject()
    private val mainViewModel : MainViewModel by inject()
    private val statusAdapter : StatusAdapter by inject()
    private val statusViewModel : StatusViewModel by inject()
    private val alertAdapter : AlertAdapter by inject()
    private val alertViewModel : AlertViewModel by inject()
    private val scheduler : SchedulerProvider by inject()
    private val trainDataService : TrainDataService by inject()

    @BeforeAll
    fun beforeAll() {
        startKoin(koinModules)
    }

    @AfterAll
    fun afterAll() {
        closeKoin()
    }

    @Test
    fun `test schedule view model injected`() {
        val testFileName = "testFiletest2313"
        scheduleViewModel.navigator = object: ScheduleNavigator {
            override fun startActivity(destination: File) {//ignore
            }
            override var appFilesDir: File? = File(testFileName)
            override var appAssets: AssetManager? = null
        }

        assertEquals(testFileName, scheduleViewModel.navigator!!.appFilesDir!!.name)
    }

    @Test
    fun `test main view model injected`() {
        // Just testing to make sure this is a real class
        assertFalse(mainViewModel.isFragmentLoaded)
        mainViewModel.addToBackstack(1)
        assertTrue(mainViewModel.isFragmentLoaded)
    }

    @Test
    fun `test status adapter`() {
        val dummyNumber = "431"
        statusAdapter.trainStatuses.add(DummyTrainStatusBuilder().number(dummyNumber).build())
        assertEquals(dummyNumber, statusAdapter.trainStatuses[0].number)
    }

    @Test
    fun `test status view model`() {
        // Mostly just stubbing to make sure it actually instantiates the model
        statusViewModel.selectedTrainLine.value = 0
        val start = statusViewModel.selectedTrainLine.value
        statusViewModel.selectedTrainLine.value = 1
        val end = statusViewModel.selectedTrainLine.value

        assertNotEquals(start, end)
    }

    @Test
    fun `test alert adapter`() {
        val testAlert = TrainAlert("d", "p")
        alertAdapter.alerts.add(testAlert)
        assertEquals(testAlert, alertAdapter.alerts[0])
    }

    @Test
    fun `test alert view model`() {
        val testAlert = TrainAlert("d", "p")
        assertEquals(0, alertViewModel.allAlerts.value!!.size)
        alertViewModel.allAlerts.value = listOf(testAlert)
        assertEquals(1, alertViewModel.allAlerts.value!!.size)
    }

    @Test
    fun `test scheduler injected`() {
        // Only do io, ui creates more issues.
        assertNotNull(scheduler.io())
    }

    @Test
    fun `test train status service`() {
        // We don't want to call this here because its an actual network service.
        assertNotNull(trainDataService)
    }
}