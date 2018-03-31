package com.idleoffice.marctrain.ui.status

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.idleoffice.marctrain.MainApp
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.retrofit.tss.TrainStatusService
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.idleoffice.marctrain.ui.base.BaseViewModel
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class StatusViewModel(app : Application,
                      schedulerProvider: SchedulerProvider,
                      private var trainStatusService: TrainStatusService):
        BaseViewModel<StatusNavigator>(app, schedulerProvider) {

    val currentTrainStatusData = MutableLiveData<List<TrainStatus>>().apply { value = emptyList() }
    private val allTrainStatusData = MutableLiveData<List<TrainStatus>>().apply { value = emptyList() }
    val selectedTrainLine = MutableLiveData<Int>().apply { value = 0 }
    private val selectedTrainDirection = MutableLiveData<Int>().apply { value = 0 }

    override fun initialize() {
        Timber.d("Init")
        doGetTrainStatus()
        super.initialize()
    }

    private val statusObservable = Observable.interval(0,10, TimeUnit.SECONDS)
            .flatMap { trainStatusService.getTrainData()
                    .onErrorResumeNext { t: Throwable ->
                        val logMessage = t.message ?: ""
                        Timber.e(t, "Error attempting to get current train status: $logMessage")
                        Observable.empty()
                    }
            }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())!!

    private fun doGetTrainStatus() {
        val statusDisposable = statusObservable.subscribe(
                { n ->
                    allTrainStatusData.value = n
                    parseCurrentTrainStatus()
                },
                {e -> Timber.e(e)})

        compositeDisposable.add(statusDisposable)
    }

    fun trainLineSelected(position: Int) {
        Timber.d("Line selected at: $position")
        selectedTrainLine.value = position
        parseCurrentTrainStatus()
    }

    fun trainDirectionSelected(position: Int) {
        Timber.d("Direction selected at: $position")
        selectedTrainDirection.value = position
        parseCurrentTrainStatus()
    }

    fun parseCurrentTrainStatus() {
        val res = getApplication<MainApp>().resources

        // value is initialized, so assert that it shouldn't be null
        val line = res.getStringArray(R.array.line_array)[selectedTrainLine.value!!]

        val direction = when(line) {
            "Brunswick" -> res.getStringArray(R.array.ew_dir_array)[selectedTrainDirection.value!!]
            else -> res.getStringArray(R.array.ns_dir_array)[selectedTrainDirection.value!!]
        }

        currentTrainStatusData.value = allTrainStatusData.value?.filter {
            (it.direction == direction && it.line == line)
        }
    }

}