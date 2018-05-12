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
                     private val trainDataService: TrainDataService) :
        BaseViewModel<AlertNavigator>(app, schedulerProvider)
{
    init {
        Timber.d("Initialized...")
    }

    val allAlerts = MutableLiveData<List<TrainAlert>>().apply { value = emptyList() }

    override fun initialize() {
        Timber.d("Init")
        doGetTrainAlerts()
        super.initialize()
    }

    private fun doGetTrainAlerts() {
        val alertDisposable = Observable
                .interval(0,
                        BuildConfig.ALERT_POLL_INTERVAL,
                        TimeUnit.SECONDS,
                        schedulerProvider.io()
                )
                .flatMap {trainDataService.getTrainAlerts() }
                .doOnError {
                    val logMessage = it.message ?: ""
                    if(logMessage.contains("HTTP 404 Not Found")) {
                        Timber.w(it, "404 attempting to get MARC alerts.")
                    } else {
                        Timber.e(it, "Error attempting to get alerts.")
                    }
                }
                .retryWhen {
                    it.flatMap {
                        Observable.timer(
                                BuildConfig.ALERT_POLL_RETRY_INTERVAL,
                                TimeUnit.SECONDS,
                                schedulerProvider.io()
                        )
                    }
                }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                { n ->
                    Timber.d("Data: $n")
                    allAlerts.value = n
                },
                { e -> Timber.e(e)}
        )

        compositeDisposable.add(alertDisposable)
    }
}