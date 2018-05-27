package com.idleoffice.marctrain.ui.schedule

import android.content.res.AssetManager
import android.os.Vibrator
import com.idleoffice.marctrain.MainApp
import com.idleoffice.marctrain.TempDirectory
import com.idleoffice.marctrain.TrampolineSchedulerProvider
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

    private val trampolineScheduler = TrampolineSchedulerProvider()

    private lateinit var ut: ScheduleViewModel

    var mockAppAssets: AssetManager = mock()

    @BeforeEach
    fun setUp() {
        ut = ScheduleViewModel(mockApp, trampolineScheduler)
        ut.navigator = TestScheduleNavigator()

        whenever(ut.app.getSystemService(any())).then { mock() as Vibrator }
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

        @Test
        fun `test appFileDir is null`() {
            ut.navigator!!.appFilesDir = null

            var exceptionOccurred = false
            try {
                ut.launchPennTable()
                fail<Any>("Should have thrown an exception")
            } catch (e: ScheduleViewModel.NullNavigatorValueException) {
                // This is desired
                exceptionOccurred = true
            }

            assertTrue(exceptionOccurred, "NullNavigatorValueException should have occurred, but did not.")
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
            ut.navigator!!.appAssets = null

            var exceptionOccurred = false
            try {
                launchTableHelper({ut.launchPennTable()})
            } catch (e: ScheduleViewModel.NullNavigatorValueException) {
                // Desired
                exceptionOccurred = true
            }

            assertTrue(exceptionOccurred)
        }
    }

    open inner class TestScheduleNavigator: ScheduleNavigator {

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