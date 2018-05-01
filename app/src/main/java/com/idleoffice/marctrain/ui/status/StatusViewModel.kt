package com.idleoffice.marctrain.ui.status

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.res.Resources
import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.idleoffice.marctrain.ui.base.BaseViewModel
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class StatusViewModel(app: Application,
                      schedulerProvider: SchedulerProvider,
                      trainDataService: TrainDataService):
        BaseViewModel<StatusNavigator>(app, schedulerProvider) {

    val currentTrainStatusData = MutableLiveData<List<TrainStatus>>()
    private val allTrainStatusData = MutableLiveData<List<TrainStatus>>()
    val selectedTrainLine = MutableLiveData<Int>()
    private val selectedTrainDirection = MutableLiveData<Int>()
    val title = MutableLiveData<String>()

    init {
        currentTrainStatusData.value = emptyList()
        allTrainStatusData.value = emptyList()
        selectedTrainLine.value = 0
        selectedTrainDirection.value = 0
        title.value = ""
    }

    val resources: Resources = app.resources

    private val statusObservable = Observable.interval(0, BuildConfig.STATUS_POLL_INTERVAL, TimeUnit.SECONDS)
            .flatMap { trainDataService.getTrainStatus() }
            .doOnError {
                Timber.e(it, "Error attempting to get current train status")
            }
            .retryWhen {
                it.flatMap {
                    Observable.timer(10, TimeUnit.SECONDS)
                }
            }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())!!

    override fun initialize() {
        Timber.d("Init")
        doGetTrainStatus(statusObservable)
        super.initialize()
    }

    fun doGetTrainStatus(statusObservable: Observable<List<TrainStatus>>) {
        val statusDisposable = statusObservable.subscribe(
                { n ->
                    allTrainStatusData.value = n
                    updateCurrentTrains()
                },
                {e -> Timber.e(e)})

        compositeDisposable.add(statusDisposable)
    }

    fun trainLineSelected(position: Int) {
        Timber.d("Line selected at: $position")
        selectedTrainLine.value = position
        updateCurrentTrains()
    }

    fun trainDirectionSelected(position: Int) {
        Timber.d("Direction selected at: $position")
        selectedTrainDirection.value = position
        updateCurrentTrains()
    }

    private fun updateCurrentTrains() {
        val line = resources.getStringArray(R.array.line_array)[selectedTrainLine.value!!]

        val direction = when(line) {
            "Brunswick" -> resources.getStringArray(R.array.ew_dir_array)[selectedTrainDirection.value!!]
            else -> resources.getStringArray(R.array.ns_dir_array)[selectedTrainDirection.value!!]
        }

        currentTrainStatusData.value = allTrainStatusData.value?.filter {
            (it.direction == direction && it.line == line)
        }

        title.value = "$line $direction"
    }

}