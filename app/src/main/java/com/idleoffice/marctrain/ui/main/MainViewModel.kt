package com.idleoffice.marctrain.ui.main

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.retrofit.tss.TrainStatusService
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.idleoffice.marctrain.ui.base.BaseViewModel
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit


class MainViewModel(
        app : Application,
        schedulerProvider: SchedulerProvider,
        private var trainStatusService: TrainStatusService)
    : BaseViewModel<MainNavigator>(app, schedulerProvider) {

    val trainStatusData = MutableLiveData<List<TrainStatus>>()

    init {
        doGetTrainStatus()
    }

    fun doGetTrainStatus() {
        val statusGetter = Observable.interval(0,10, TimeUnit.SECONDS)
                .flatMap { trainStatusService.getTrainData()
                        .onErrorResumeNext { t: Throwable ->
                            val msg = "Error attempting to get current train status"
                            val logMessage = t.message ?: ""

                            Timber.e(t, msg)
                            Observable.empty()
                        }
                }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe({ n -> trainStatusData.value = n
                                Timber.d("Received data: %s", n) },
                        {e -> Timber.e(e)})

        compositeDisposable.add(statusGetter)
    }

}