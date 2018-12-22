/*
 * Copyright (c) 2018 IdleOffice Inc.
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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.idleoffice.marctrain.ui.schedule

import android.content.res.AssetManager
import com.idleoffice.marctrain.MainApp
import com.idleoffice.marctrain.TempDirectory
import com.idleoffice.marctrain.TrampolineContextProvider
import com.idleoffice.marctrain.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File
import java.nio.file.Path

@ExtendWith(TempDirectory::class)
internal class ScheduleViewModelTest(@TempDirectory.TempDir val tempDir: Path) {

    val mockApp: MainApp = mock()

    private val trampolineScheduler = TrampolineContextProvider()

    private lateinit var ut: ScheduleViewModel

    var mockAppAssets: AssetManager = mock()

    @BeforeEach
    fun setUp() {
        ut = ScheduleViewModel(mockApp, trampolineScheduler)
        ut.navigator = TestScheduleNavigator()

//        whenever(ut.app.getSystemService(any())).then { mock() as Vibrator }
    }
    
    @Nested
    inner class GenerateTempFileTest {

        @Test
        fun `generate tempFile null app file directory`() {
            ut.navigator!!.appFilesDir = null

            try {
                ut.generateTempFile("")
                fail<Any>()
            } catch (e: ScheduleViewModel.NullNavigatorValueException) {
                // Good!
            }
        }
        
        @Test
        fun `generate temp file happy`() {
            val tempFileName = "test"
            val tempFile = tempDir.toFile()
            // TODO note, JUnit 5 does not have a TemporaryFolder equivalent yet
            ut.navigator!!.appFilesDir = tempFile  // File(appFileName)
            val f = ut.generateTempFile(tempFileName)

            assertEquals("${tempFile.absolutePath}/tables/$tempFileName", f.absolutePath)
        }
    }

    @Nested
    inner class LaunchTableTest {

        private val testStreamData = "asdfasdfa"

        @BeforeEach
        fun setup() {
            setUp()
        }

        private fun launchTableHelper(unit: () -> Unit): TestScheduleNavigator {
            val utNav = ut.navigator as TestScheduleNavigator

            // Sanity check, make sure UTs aren't bad
             assertEquals(0, utNav.startActivityCalls)

            whenever(mockAppAssets.open(any())).then { testStreamData.byteInputStream() }
            unit.invoke()
            assertEquals(testStreamData, utNav.currentfile!!.readText())
            assertEquals(1, utNav.startActivityCalls)

            return utNav
        }

        @Test
        fun `launchPennTable happy path`() {

            val utNav = launchTableHelper({ut.launchPennTable()})
            assertEquals(ScheduleViewModel.pennFileName, utNav.currentfile!!.name)
        }

        @Test
        fun `launchCamdenTable happy path`() {
            val utNav = launchTableHelper({ut.launchCamdenTable()})
            assertEquals(ScheduleViewModel.camdenFileName, utNav.currentfile!!.name)
        }

        @Test
        fun `launchBrunswickTable happy path`() {
            val utNav = launchTableHelper({ut.launchBrunswickTable()})
            assertEquals(ScheduleViewModel.brunswickFileName, utNav.currentfile!!.name)
        }

        @Test
        fun `launchTable null assets`() {
            val utNav = ut.navigator as TestScheduleNavigator
            utNav.appAssets = null
            ut.launchPennTable()
            assertNull(utNav.currentfile)
        }

        @Test
        fun `appFileDir is null`() {
            val utNav = ut.navigator as TestScheduleNavigator
            utNav.appFilesDir = null
            ut.launchPennTable()
            assertNull(utNav.currentfile)
        }
    }

    open inner class TestScheduleNavigator: ScheduleNavigator {
        override fun vibrateTap() {}

        var startActivityCalls = 0
        var currentfile: File? = null

        override fun startPdfActivity(destination: File) {
            startActivityCalls++
            currentfile = destination
        }

        override var appFilesDir: File? = tempDir.toFile()
        override var appAssets: AssetManager? = mockAppAssets
    }
}