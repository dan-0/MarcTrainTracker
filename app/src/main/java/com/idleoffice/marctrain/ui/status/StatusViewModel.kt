/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * StatusViewModel.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain.ui.status

import androidx.lifecycle.MutableLiveData
import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.coroutines.CoroutineContextProvider
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.idling.IdlingResource
import com.idleoffice.marctrain.network.NetworkProvider
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.ui.base.BaseViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class StatusViewModel(
        coroutineContextProvider: CoroutineContextProvider,
        private val trainDataService: TrainDataService,
        private val networkProvider: NetworkProvider,
        private val idlingResource: IdlingResource
) : BaseViewModel(coroutineContextProvider) {

    val allTrainStatusData = MutableLiveData<List<TrainStatus>>().apply { listOf<TrainStatus>() }
    val selectedTrainLine = MutableLiveData<Int>().apply { value = 0 }
    val selectedTrainDirection = MutableLiveData<Int>().apply { value = 0 }

    override fun initialize() {
        super.initialize()
        Timber.d("Init")
        doGetTrainStatus()
    }

    private suspend fun loadTrainData() {
        Timber.d("Loading train data")
        val trains = runCatching {
            trainDataService.getTrainStatus().await()
        }.getOrElse {
            Timber.e(it, "Error getting train status")
            return
        }

        allTrainStatusData.postValue(trains)
    }

    private fun doGetTrainStatus() {
        ioScope.launch {
            while (true) {
                idlingResource.startIdlingAction()
                val delayInterval = if (networkProvider.isNetworkConnected()) {
                    BuildConfig.STATUS_POLL_INTERVAL
                } else {
                    BuildConfig.STATUS_POLL_RETRY_INTERVAL
                }
                loadTrainData()
                idlingResource.stopIdlingAction()
                delay(delayInterval)
            }
        }.invokeOnCompletion {
            it?.run {
                if (this !is CancellationException) {
                    Timber.e(it, "Exception on Status completion")
                }
            }
            idlingResource.stopIdlingAction()
        }
    }
}