package com.idleoffice.marctrain.ui.status

import android.support.v7.widget.DefaultItemAnimator
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.RobolectricTest
import com.idleoffice.marctrain.TestSchedulerProvider
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.data.tools.TrainLineTools.Companion.DIRECTION_FROM_DC
import com.idleoffice.marctrain.data.tools.TrainLineTools.Companion.DIRECTION_TO_DC
import com.idleoffice.marctrain.data.tools.TrainLineTools.Companion.PENN_LINE_IDX
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import kotlinx.android.synthetic.main.fragment_status_coordinator.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil

internal class StatusFragmentTest: RobolectricTest() {
    private lateinit var ut : StatusFragment
    private val helper = StatusTestHelper()

    @Before
    fun setUp() {
        ut = StatusFragment()

        StandAloneContext.loadKoinModules(helper.statusTestModule)
        SupportFragmentTestUtil.startFragment(ut)
    }

    @After
    fun tearDown() {
        StandAloneContext.closeKoin()
    }

    @Test
    fun `test fragment load`() {
        // Check some specific settings that we set during initialization
        assertTrue(ut.trainStatusList.adapter is StatusAdapter)
        assertTrue(ut.trainStatusList.itemAnimator is DefaultItemAnimator)
        assertNotNull(ut.directionSpinner.adapter)
        assertNotNull(ut.lineSpinner.adapter)

        // Make sure we're equalling the first appropriate item
        val initLine = ut.resources.getStringArray(R.array.line_array)[PENN_LINE_IDX]
        assertEquals(initLine, ut.lineSpinner.selectedItem)

        // Make sure we're equalling the first appropriate item
        val initDirection = ut.resources.getStringArray(R.array.ns_dir_array)[DIRECTION_FROM_DC]
        assertEquals(initDirection, ut.directionSpinner.selectedItem)
    }

    @Test
    fun `east west direction selected`() {
        val ewLine = ut.resources.getStringArray(R.array.line_array).indexOf("Brunswick")
        ut.lineSpinner.setSelection(ewLine)

        val ewDirection = ut.resources.getStringArray(R.array.ew_dir_array)[0]
        assertEquals(ewDirection, ut.directionSpinner.selectedItem)
    }

    @Test
    fun `proper location to direction`() {
        val lines = ut.resources.getStringArray(R.array.line_array)

        lines.forEachIndexed { i, s ->
            ut.lineSpinner.setSelection(i)
            ut.directionSpinner.setSelection(DIRECTION_FROM_DC)
            when(s) {
                "Brunswick" -> {
                    assertEquals("$s East", ut.statusCollapsing.title)
                    ut.directionSpinner.setSelection(DIRECTION_TO_DC)
                    assertEquals("$s West", ut.statusCollapsing.title)
                }

                else -> {
                    assertEquals("$s North", ut.statusCollapsing.title)
                    ut.directionSpinner.setSelection(DIRECTION_TO_DC)
                    assertEquals("$s South", ut.statusCollapsing.title)
                }
            }
        }
    }

    @Test
    fun `results displayed`() {
        val testValue = helper.testTrainStatus
        val lines = ut.resources.getStringArray(R.array.line_array)
        val directions = ut.resources.getStringArray(R.array.ns_dir_array)

        ut.lineSpinner.setSelection(lines.indexOf(testValue.line))
        ut.directionSpinner.setSelection(directions.indexOf(testValue.direction))

        helper.ts.triggerActions()
        helper.ts.triggerActions()

        val adapter = ut.trainStatusList.adapter as StatusAdapter
        assertEquals(1, adapter.itemCount)
        assertEquals(testValue, adapter.trainStatuses[0])
    }

    private class StatusTestHelper {

        val testTrainStatus = TrainStatus(
                "435",
                "Penn",
                "South",
                "BWI Rail Station",
                "3:36 PM",
                "On Time",
                "None",
                "03:30 PM",
                ""
        )

        val ts = TestScheduler()
        private val scheduler = TestSchedulerProvider(ts)

        val statusTestModule : Module = applicationContext {
            viewModel { StatusViewModel(get(), scheduler, object: TrainDataService {
                override fun getTrainStatus(): Observable<List<TrainStatus>> {
                    return Observable.fromCallable { listOf(testTrainStatus) }
                }

                override fun getTrainAlerts(): Observable<List<TrainAlert>> {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            }) }
        }
    }



}