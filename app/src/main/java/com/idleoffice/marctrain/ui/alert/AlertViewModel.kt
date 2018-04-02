package com.idleoffice.marctrain.ui.alert

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.idleoffice.marctrain.ui.base.BaseViewModel
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class AlertViewModel(app: Application,
                     schedulerProvider: SchedulerProvider,
                     trainDataService: TrainDataService) :
        BaseViewModel<AlertNavigator>(app, schedulerProvider)
{
    init {
        Timber.d("Initialized...")
    }

    val allAlerts = MutableLiveData<List<TrainAlert>>().apply { value = emptyList() }

    private val alertObservable = Observable
            .interval(0, BuildConfig.ALERT_POLL_INTERVAL, TimeUnit.SECONDS)
            .flatMap {
                trainDataService.getTrainAlerts()
                        .onErrorResumeNext { t: Throwable ->
                            val logMessage = t.message ?: ""
                            if(logMessage.contains("HTTP 404 Not Found")) {
                                Timber.w("404 attempting to get MARC alerts: $logMessage")
                            } else {
                                Timber.e(t, "Error attempting to get alerts: $logMessage")
                            }
                            Observable.empty()
                        }
            }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())

    override fun initialize() {
        Timber.d("Init")
        doGetTrainAlerts()
        super.initialize()
    }

    private fun doGetTrainAlerts() {
        val alertDisposable = alertObservable.subscribe(
                { n ->
                    Timber.d("Data: $n")
                    allAlerts.value = n
                    navigator?.hideLoading()
                },
                { e -> Timber.e(e)}
        )

        compositeDisposable.add(alertDisposable)
    }


}