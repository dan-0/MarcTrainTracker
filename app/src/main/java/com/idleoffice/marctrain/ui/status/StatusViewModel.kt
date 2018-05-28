package com.idleoffice.marctrain.ui.status

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.res.Resources
import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.Const.Companion.BRUNSWICK_STATIONS
import com.idleoffice.marctrain.Const.Companion.CAMDEN_STATIONS
import com.idleoffice.marctrain.Const.Companion.PENN_STATIONS
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.data.comparator.TrainStatusComparator
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.observeSubscribe
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.idleoffice.marctrain.ui.base.BaseViewModel
import io.reactivex.Flowable
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class StatusViewModel(app: Application,
                      schedulerProvider: SchedulerProvider,
                      private val trainDataService: TrainDataService) :
        BaseViewModel<StatusNavigator>(app, schedulerProvider) {

    val currentTrainStatusData = MutableLiveData<List<TrainStatus>>().apply { value = emptyList() }
    val allTrainStatusData = MutableLiveData<List<TrainStatus>>().apply { value = emptyList() }
    val selectedTrainLine = MutableLiveData<Int>().apply { value = 0 }
    val selectedTrainDirection = MutableLiveData<Int>().apply { value = 0 }
    val title = MutableLiveData<String>().apply { value = "" }

    val resources: Resources = app.resources

    override fun initialize() {
        super.initialize()
        Timber.d("Init")
        doGetTrainStatus()
    }


    private fun doGetTrainStatus() {
        val statusDisposable = Observable
                .interval(0, BuildConfig.STATUS_POLL_INTERVAL, TimeUnit.SECONDS, schedulerProvider.io())
                .flatMap { trainDataService.getTrainStatus() }
                .doOnError {
                    Timber.w(it, "Error attempting to get current train status: $it")
                }
                .retryWhen {
                    it.flatMap {
                        Observable.timer(
                                BuildConfig.STATUS_POLL_RETRY_INTERVAL,
                                TimeUnit.SECONDS,
                                schedulerProvider.io())
                    }
                }
                .observeSubscribe(schedulerProvider)
                .subscribe(
                { n ->
                    allTrainStatusData.value = n
                    compositeDisposable.add(updateCurrentTrains.subscribe())
                },
                { e ->
                    Timber.e(e)
                })

        compositeDisposable.add(statusDisposable)
    }

    fun trainLineSelected(position: Int) {
        Timber.d("Line selected at: $position")
        selectedTrainLine.value = position
    }

    fun trainDirectionSelected(position: Int) {
        Timber.d("Direction selected at: $position")
        selectedTrainDirection.value = position
        compositeDisposable.add(updateCurrentTrains.subscribe())
    }

    private val updateCurrentTrains = Flowable.fromCallable {
        val selectedLine = selectedTrainLine.value!!
        val line = resources.getStringArray(R.array.line_array)[selectedLine]

        val lineDirectionValue = selectedTrainDirection.value!!

        val direction = when(lineDirectionValue) {
            2 -> resources.getStringArray(R.array.ew_dir_array)[lineDirectionValue]
            else -> resources.getStringArray(R.array.ns_dir_array)[lineDirectionValue]
        }

        var compareArray = when(selectedLine) {
            0 -> PENN_STATIONS
            1 -> CAMDEN_STATIONS
            else -> BRUNSWICK_STATIONS
        }

        val toWashington = lineDirectionValue == 1
        if (!toWashington) {
            compareArray = compareArray.asReversed()
        }

        val current =  allTrainStatusData.value?.filter {
            (it.direction == direction && it.line == line)
        }?.sortedWith(TrainStatusComparator(compareArray))

        currentTrainStatusData.value = current

        // Don't set it if its the same, otherwise we'll trigger the observable behavior
        if (title.value != "$line $direction") {
            title.value = "$line $direction"
        }
    }.onBackpressureLatest()
}