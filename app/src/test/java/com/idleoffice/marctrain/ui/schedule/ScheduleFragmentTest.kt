package com.idleoffice.marctrain.ui.schedule

import android.content.res.AssetManager
import com.idleoffice.marctrain.RobolectricTest
import com.idleoffice.marctrain.TestSchedulerProvider
import com.idleoffice.marctrain.rx.SchedulerProvider
import io.reactivex.schedulers.TestScheduler
import kotlinx.android.synthetic.main.fragment_schedule.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
import java.io.File

class ScheduleFragmentTest: RobolectricTest() {

    private lateinit var ut: ScheduleFragment
    private val helper = ScheduleTestHelper()


    @Before
    fun setUp() {
        loadKoinModules(helper.scheduleTestModule)
        ut = ScheduleFragment()
        SupportFragmentTestUtil.startFragment(ut)
    }

    @After
    fun tearDown() {
        closeKoin()
    }

    @Test
    fun `test line schedules selected`() {
        // Setup te objects for each schedule that can be selected
        val testObjects = arrayOf(
                Pair("pennFull.pdf", ut.btnTablesPenn),
                Pair("camdenFull.pdf", ut.btnTablesCamden),
                Pair("brunswickFull.pdf", ut.btnTablesBrunswick)
        )

        for (o in testObjects) {
            val testNavigator = ScheduleTestHelper.TestScheduleNavigator(ut.appFilesDir, ut.appAssets)
            ut.fragViewModel.navigator = testNavigator
            o.second.performClick()
            helper.ts.triggerActions()

            Assert.assertEquals(1, testNavigator.startPdfCalledTimes)
            Assert.assertEquals(o.first, testNavigator.lastPdfFilePath)
        }

    }

    private class ScheduleTestHelper {
        val ts = TestScheduler()
        private val scheduler = TestSchedulerProvider(ts)

        val scheduleTestModule = applicationContext {
            bean { scheduler as SchedulerProvider }
        }

        class TestScheduleNavigator(override var appFilesDir: File?, override var appAssets: AssetManager?): ScheduleNavigator {
            var startPdfCalledTimes = 0
            var lastPdfFilePath = ""

            override fun startPdfActivity(destination: File) {
                startPdfCalledTimes++
                lastPdfFilePath = destination.name
            }
        }
    }
}