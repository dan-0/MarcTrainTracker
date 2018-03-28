package com.idleoffice.marctrain.ui.status

import android.app.Application
import android.arch.lifecycle.MutableLiveData
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

    val trainStatusData = MutableLiveData<List<TrainStatus>>()
    val selectedTrainLine = MutableLiveData<Int>()

    override fun initialize() {
        Timber.d("Init")
        doGetTrainStatus()
        super.initialize()
    }

    val statusObservable = Observable.interval(0,10, TimeUnit.SECONDS)
            .flatMap { trainStatusService.getTrainData()
                    .onErrorResumeNext { t: Throwable ->
                        val msg = "Error attempting to get current train status: %s"
                        val logMessage = t.message ?: ""

                        Timber.e(t, msg, logMessage)
                        Observable.empty()
                    }
            }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())

    private fun doGetTrainStatus() {
        val statusDisposable = statusObservable.subscribe(
                { n -> trainStatusData.value = n },
                {e -> Timber.e(e)})

        compositeDisposable.add(statusDisposable)
    }

    fun trainLineSelected(position: Int) {
        Timber.d("Item selected at: %d", position)
        selectedTrainLine.value = position
    }

}