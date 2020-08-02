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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idleoffice.marctrain.coroutines.CoroutineContextProvider
import com.idleoffice.marctrain.idling.IdlingResource
import com.idleoffice.marctrain.network.NetworkProvider
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.threeten.bp.Duration
import timber.log.Timber

class AlertViewModel(
    private val coroutineContextProvider: CoroutineContextProvider,
    private val trainDataService: TrainDataService,
    private val networkProvider: NetworkProvider,
    private val idlingResource: IdlingResource
) : ViewModel() {

    private val _state = MutableLiveData<AlertViewState>(AlertViewState.Init)
    val state: LiveData<AlertViewState> = _state

    fun loadAlerts() = doGetTrainAlerts()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable, "Exception on Alert completion")

        _state.postValue(AlertViewState.Error)

        idlingResource.stopIdlingAction()
    }

    private fun doGetTrainAlerts() {

        viewModelScope.launch(coroutineContextProvider.io) {
            while (true) {
                val delayInterval = if (networkProvider.isNetworkConnected()) {
                    loadAlertData()
                    ALERT_POLL_INTERVAL
                } else {
                    ALERT_POLL_RETRY_INTERVAL
                }

                delay(delayInterval.toMillis())
            }
        }
    }

    private suspend fun loadAlertData() = supervisorScope {
        launch(exceptionHandler) {
            idlingResource.startIdlingAction()

            val result = trainDataService.getTrainAlerts()

            val newState = if (result.isNotEmpty()) {
                AlertViewState.Content(result)
            } else {
                AlertViewState.NoTrainsFound
            }

            _state.postValue(newState)

            idlingResource.stopIdlingAction()
        }

    }

    companion object {
        private val ALERT_POLL_INTERVAL = Duration.ofMinutes(1)
        private val ALERT_POLL_RETRY_INTERVAL = Duration.ofSeconds(10)
    }
}

