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
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.coroutines.ContextProvider
import com.idleoffice.marctrain.network.NetworkProvider
import com.idleoffice.marctrain.ui.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

class AlertViewModel(contextProvider: ContextProvider,
                     private val trainDataService: TrainDataService,
                     private val networkProvider: NetworkProvider
) : BaseViewModel<AlertNavigator>(contextProvider)
{
    val allAlerts = MutableLiveData<List<TrainAlert>>().apply { value = emptyList() }

    override fun initialize() {
        super.initialize()
        Timber.d("Init")
        doGetTrainAlerts()
    }

    private suspend fun loadAlertData() {
        val call = trainDataService.getTrainAlerts()

        val alerts = try {
            call.await()
        } catch (e: IOException) {
            Timber.w(e, "Error retrieving alerts")
            return
        }

        withContext(contextProvider.ui) {
            allAlerts.value = alerts
        }
    }

    private fun doGetTrainAlerts() {

        ioScope.launch {
            while (true) {
                val delayInterval = if (networkProvider.isNetworkConnected()) {
                    loadAlertData()
                    BuildConfig.STATUS_POLL_INTERVAL
                } else {
                    BuildConfig.STATUS_POLL_RETRY_INTERVAL
                }
                delay(delayInterval)
            }
        }
    }
}