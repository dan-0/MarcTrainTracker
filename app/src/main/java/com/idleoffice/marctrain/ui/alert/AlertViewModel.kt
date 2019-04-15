/*
 * Copyright (c) 2019 IdleOffice Inc.
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
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.idleoffice.marctrain.ui.alert

import androidx.lifecycle.MutableLiveData
import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.coroutines.CoroutineContextProvider
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.idling.IdlingResource
import com.idleoffice.marctrain.network.NetworkProvider
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.ui.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

class AlertViewModel(coroutineContextProvider: CoroutineContextProvider,
                     private val trainDataService: TrainDataService,
                     private val networkProvider: NetworkProvider,
                     private val idlingResource: IdlingResource
) : BaseViewModel(coroutineContextProvider) {
    val allAlerts = MutableLiveData<List<TrainAlert>>()

    override fun initialize() {
        super.initialize()
        Timber.d("Init")
        doGetTrainAlerts()
    }

    private suspend fun loadAlertData() {
        val alerts = runCatching {
            trainDataService.getTrainAlerts().await()
        }.getOrElse {
            Timber.e(it, "Error getting train alert data")
            return
        }

        allAlerts.postValue(alerts)
    }

    private fun doGetTrainAlerts() {

        ioScope.launch {
            while (true) {
                idlingResource.startIdlingAction()
                val delayInterval = if (networkProvider.isNetworkConnected()) {
                    loadAlertData()
                    BuildConfig.ALERT_POLL_INTERVAL
                } else {
                    BuildConfig.ALERT_POLL_RETRY_INTERVAL
                }
                idlingResource.stopIdlingAction()
                delay(delayInterval)
            }
        }
    }
}