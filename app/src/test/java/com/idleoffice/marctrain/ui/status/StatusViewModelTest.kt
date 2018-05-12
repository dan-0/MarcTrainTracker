package com.idleoffice.marctrain.ui.status

import android.content.res.Resources
import com.idleoffice.marctrain.*
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import timber.log.Timber
import java.util.concurrent.TimeUnit

@ExtendWith(InstantTaskExecutorExtension::class)
internal class StatusViewModelTest {

    @Mock
    private lateinit var mockApp : MainApp

    @Mock
    private lateinit var mockResources: Resources

    init {
        MockitoAnnotations.initMocks(this)
        Timber.plant(Timber.DebugTree())
    }

    private val stubScheduler = TrampolineSchedulerProvider()

    private val stubTrainDataService = object: TrainDataService {
        override fun getTrainStatus(): Observable<List<TrainStatus>> {
            return Observable.fromArray(listOf())
        }

        override fun getTrainAlerts(): Observable<List<TrainAlert>> {
            return Observable.fromArray(listOf())
        }
    }

    @BeforeEach
    fun beforeEach() {
        // We're doing this here so we can simulate bad values later
        whenever(mockResources.getStringArray(R.array.line_array)).thenReturn(stubLineArray)
        whenever(mockResources.getStringArray(R.array.ns_dir_array)).thenReturn(stubNsDirArray)
        whenever(mockResources.getStringArray(R.array.ew_dir_array)).thenReturn(stubEwDirArray)
        whenever(mockApp.resources).thenReturn(mockResources)
    }

    /**
     * Test the initialization of the app. Primarily the doGetTrainStatus functionality
     */
    @Nested
    inner class ViewInitializeHelper {
        private val ts = TestScheduler()
        val scheduler = TestSchedulerProvider(ts)

        val dummyTrainStatusNumber = "0"
        val dummyTrainStatusNumber2 = "-1"
        private val trainDataService = object: TrainDataService {
            var counter = 0
            var errorOccurred = false

            val dummyTrainStatus = DummyTrainStatusBuilder()
                    .number(dummyTrainStatusNumber)
                    .build()

            val dummyTrainStatus2 = DummyTrainStatusBuilder()
                    .number(dummyTrainStatusNumber2)
                    .build()

            val dummyError = Exception("Dummy")

            override fun getTrainStatus(): Observable<List<TrainStatus>> {
                counter++
                System.out.println("counter: $counter, time: ${scheduler.io().now(TimeUnit.SECONDS)}")
                return when {
                    counter == 3 -> {
                        errorOccurred = true
                        throw dummyError
                    }
                    counter == 4 -> {
                        whenever(mockResources.getStringArray(R.array.line_array))
                                .thenThrow(Exception("Dummy2"))
                        Observable.fromArray(listOf(dummyTrainStatus))
                    }
                    counter > 4 -> Observable.fromArray(listOf(dummyTrainStatus2))
                    else -> Observable.fromArray(listOf(dummyTrainStatus))
                }
            }
            override fun getTrainAlerts(): Observable<List<TrainAlert>> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        private val ut = StatusViewModel(mockApp, scheduler, trainDataService)

        init {
            ut.viewInitialize()
            ts.advanceTimeBy(1, TimeUnit.SECONDS)
        }


        fun `test first and second return dummyTrainStatus`() {
            // Make sure we get our first event value: dummyTrainStatus
            assertEquals(dummyTrainStatusNumber, ut.allTrainStatusData.value!![0].number)

            // Second event: dummyTrainStatus
            ts.advanceTimeBy(BuildConfig.STATUS_POLL_INTERVAL, TimeUnit.SECONDS)
            assertEquals(dummyTrainStatusNumber, ut.allTrainStatusData.value!![0].number)
        }

        fun `test errors from third and fourth events are handled`() {
            // Third event: error
            ts.advanceTimeBy(BuildConfig.STATUS_POLL_INTERVAL, TimeUnit.SECONDS)
            assertTrue(trainDataService.errorOccurred)

            // Fourth event: Exception from updateCurrentTrains, fails, then continues
            // using retry interval to ensure error timing
            ts.advanceTimeBy(BuildConfig.STATUS_POLL_RETRY_INTERVAL, TimeUnit.SECONDS)
        }

        fun `test fifth event provides new train status`() {
            // Fifth event: dummyTrainStatus2
            // using retry interval to ensure error timing
            ts.advanceTimeBy(BuildConfig.STATUS_POLL_RETRY_INTERVAL, TimeUnit.SECONDS)
            assertEquals(dummyTrainStatusNumber2, ut.allTrainStatusData.value!![0].number)
        }

        fun `assert number of calls`(number: Int) {
            assertEquals(number, trainDataService.counter)
        }
    }

    @Test
    fun `test ordered set of events from doGetTrainStatus`() {
        val helper = ViewInitializeHelper()
        helper.`test first and second return dummyTrainStatus`()
        helper.`test errors from third and fourth events are handled`()
        helper.`test fifth event provides new train status`()
        // Assert we have 5 calls
        helper.`assert number of calls`(5)
    }

    @Test
    fun `test train line selected`() {
        val ut = StatusViewModel(mockApp, stubScheduler, stubTrainDataService)

        for (i in stubLineArray.indices) {
            val line = stubLineArray[i]
            ut.trainLineSelected(i)

            assertEquals(i, ut.selectedTrainLine.value, "Bad line state")

            val directionArray = when(line) {
                "Brunswick" -> stubEwDirArray
                else -> stubNsDirArray
            }

            assertEquals(directionArray[0],
                    directionArray[ut.selectedTrainDirection.value!!],
                    "Bad direction state")

            val badTitle = "Invalid title."
            when(line) {
                "Brunswick" -> {
                    assertEquals("Brunswick East", ut.title.value, badTitle)
                }
                else -> {
                    assertEquals("$line ${directionArray[0]}", ut.title.value, badTitle)
                }
            }
        }
    }

    @Test
    fun `test train direction selected`() {
        val ut = StatusViewModel(mockApp, stubScheduler, stubTrainDataService)

        // Test North South
        val nsLine = "Penn"
        ut.trainLineSelected(stubLineArray.indexOf(nsLine))
        for(i in stubNsDirArray.indices) {
            val direction = stubNsDirArray[i]
            ut.trainDirectionSelected(i)

            when(direction) {
                "North" -> assertTrue(true)
                "South" -> assertTrue(true)
                else -> fail("Invalid direction: $direction")
            }

            assertEquals(i, ut.selectedTrainDirection.value)
            assertEquals("$nsLine $direction", ut.title.value)
        }

        // Test East West
        val ewLine = "Brunswick"
        ut.trainLineSelected(stubLineArray.indexOf(ewLine))
        for(i in stubEwDirArray.indices) {
            val direction = stubEwDirArray[i]
            ut.trainDirectionSelected(i)

            assertEquals(i, ut.selectedTrainDirection.value)

            when(direction) {
                "East" -> assertTrue(true)
                "West" -> assertTrue(true)
                else -> fail("Invalid direction: $direction")
            }

            assertEquals("$ewLine $direction", ut.title.value)
        }
    }

    private val stubLineArray = arrayOf("Penn", "Camden", "Brunswick")
    private val stubNsDirArray = arrayOf("North", "South")
    private val stubEwDirArray = arrayOf("East", "West")
}
