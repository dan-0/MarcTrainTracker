/*
 * Copyright (c) 2018 IdleOffice Inc.
 *
 * AlertViewModel.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain.ui.alert

import androidx.lifecycle.MutableLiveData
import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.observeSubscribe
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.idleoffice.marctrain.ui.base.BaseViewModel
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class AlertViewModel(schedulerProvider: SchedulerProvider,
                     private val trainDataService: TrainDataService) :
        BaseViewModel<AlertNavigator>(schedulerProvider)
{
    val allAlerts = MutableLiveData<List<TrainAlert>>().apply { value = emptyList() }

    override fun initialize() {
        super.initialize()
        Timber.d("Init")
        doGetTrainAlerts()
    }

    private fun doGetTrainAlerts() {
        val alertDisposable = Observable
                .interval(0, BuildConfig.ALERT_POLL_INTERVAL, TimeUnit.SECONDS, schedulerProvider.io())
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
                .observeSubscribe(schedulerProvider)
                .subscribe(
                {
                    Timber.d("Data: $it")
                    allAlerts.value = it
                },
                { Timber.e(it) }
        )

        compositeDisposable.add(alertDisposable)
    }
}