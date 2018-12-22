/*
 * Copyright (c) 2018 IdleOffice Inc.
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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.idleoffice.marctrain.ui.status

import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.coroutines.ContextProvider
import com.idleoffice.marctrain.network.NetworkProvider
import com.idleoffice.marctrain.ui.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

class StatusViewModel(
        contextProvider: ContextProvider,
        private val trainDataService: TrainDataService,
        private val networkProvider: NetworkProvider
) : BaseViewModel<StatusNavigator>(contextProvider) {

    val allTrainStatusData = MutableLiveData<List<TrainStatus>>().apply { value = emptyList() }
    val selectedTrainLine = MutableLiveData<Int>().apply { value = 0 }
    val selectedTrainDirection = MutableLiveData<Int>().apply { value = 0 }

    override fun initialize() {
        super.initialize()
        Timber.d("Init")
        doGetTrainStatus()
    }

    private suspend fun loadTrainData() {
        Timber.d("Loading train data")
        val call = trainDataService.getTrainStatus()
        val trains = try {
            call.await()
        } catch (e: IOException) {
            Timber.w(e, "Error getting train information.")
            return
        }
        withContext(contextProvider.ui) {
            allTrainStatusData.value = trains
        }
    }

    private fun doGetTrainStatus() {

        ioScope.launch {
            while (true) {
                val delayInterval = if (networkProvider.isNetworkConnected()) {
                    loadTrainData()
                    BuildConfig.STATUS_POLL_INTERVAL
                } else {
                    BuildConfig.STATUS_POLL_RETRY_INTERVAL
                }
                delay(delayInterval)
            }
        }
    }
}