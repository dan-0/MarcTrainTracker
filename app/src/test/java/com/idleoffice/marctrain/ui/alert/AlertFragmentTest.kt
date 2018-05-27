package com.idleoffice.marctrain.ui.alert

import android.support.v7.widget.DefaultItemAnimator
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.RobolectricTest
import com.idleoffice.marctrain.TestSchedulerProvider
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import kotlinx.android.synthetic.main.fragment_alert.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil

internal class AlertFragmentTest: RobolectricTest() {

    private lateinit var ut : AlertFragment
    private val helper = AlertTestHelper()

    @Before
    fun setUp() {
        StandAloneContext.loadKoinModules(helper.alertTestModule)
        ut = AlertFragment()
        SupportFragmentTestUtil.startFragment(ut)
    }

    @After
    fun tearDown() {
        StandAloneContext.closeKoin()
    }

    @Test
    fun `test fragment load`() {
        assertTrue(ut.trainAlertList.adapter is AlertAdapter)
        assertTrue(ut.trainAlertList.itemAnimator is DefaultItemAnimator)

        assertEquals(ut.getString(R.string.alerts), ut.alertsToolbar.title)
    }

    @Test
    fun `results displayed`() {
        val testValue = helper.testAlert

        val adapter = ut.trainAlertList.adapter as AlertAdapter
        assertEquals(0, adapter.itemCount)
        helper.ts.triggerActions()

        assertEquals(1, adapter.itemCount)
        assertEquals(testValue, adapter.alerts[0])
    }

    private class AlertTestHelper {
        val testAlert = TrainAlert(
                "Alert Data",
                "Sun, 27 May 2018 15:50:43 GMT"
        )

        val ts = TestScheduler()
        private val scheduler = TestSchedulerProvider(ts)

        val alertTestModule : Module = applicationContext {
            viewModel {
                AlertViewModel(get(), scheduler, object : TrainDataService {
                    // Stub
                    override fun getTrainStatus(): Observable<List<TrainStatus>> {
                        TODO("not implemented")
                    }

                    override fun getTrainAlerts(): Observable<List<TrainAlert>> {
                        println("Got alerts")
                        return Observable.fromCallable { listOf(testAlert) }
                    }
                })
            }
        }
    }
}