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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.threeten.bp.Duration
import timber.log.Timber

class AlertViewModel(
    private val coroutineContextProvider: CoroutineContextProvider,
    private val trainDataService: TrainDataService,
    private val networkProvider: NetworkProvider,
    private val idlingResource: IdlingResource,
    private val alertRepo: AlertRepo = AlertRepo()
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
                alertRepo.fetchAlertData()
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

class AlertRepo {

    private val _data = MutableStateFlow<AlertRepoState>(AlertRepoState.Init)
    val data: StateFlow<AlertRepoState> = _data

    suspend fun fetchAlertData() {
        val mainDoc = Jsoup.connect("$BASE_MTA_URL$SERVICE_ALERT_PATH").get()

        val alertsRows = mainDoc.getElementById(ACTIVE_ALERT_ID)?.children()

        if (alertsRows.isNullOrEmpty()) {
            Timber.e("Empty alert rows")
            return
        }

        val marcAlerts: List<Element> = alertsRows.mapNotNull {
            if (it.childNodeSize() < 2) {
                Timber.e("Unexpected node size of serviceAlertTd: ${it.data()}")
                return@mapNotNull null
            }

            val descriptionChild = it.child(1)
            if (!descriptionChild.text().contains("MARC:", true)) {
                return@mapNotNull null
            }

            it
        }

        val basicAlerts = marcAlerts.mapNotNull { it ->
            val dataNodes = it.childNodes().filterNot { node -> node is TextNode }
            if (dataNodes.size != 3) {
                Timber.e("Expected child node missing: $dataNodes")
                return@mapNotNull null
            }

            val titleElement = dataNodes[0] as? Element

            if (titleElement?.childNodeSize() != 1) {
                Timber.e("No link node found: $titleElement")
                return@mapNotNull null
            }

            val link = titleElement.childNode(0).attr("abs:href")
            val title = titleElement.text()

            val lineNode = dataNodes[1] as? Element
            val line = lineNode?.text()

            val dateNode = dataNodes[2] as? Element
            val date = dateNode?.text()

            if (line.isNullOrBlank() || date.isNullOrBlank() || link.isNullOrBlank()) {
                Timber.e("Missing link, line or date nodes: $dataNodes")
                return@mapNotNull null
            }

            BasicAlert(
                title = title,
                line = line,
                dateTime = date,
                path = link
            )
        }

        _data.value = AlertRepoState.Content(basicAlerts)
    }

    companion object {
        const val ACTIVE_ALERT_ID = "active-alerts"
        const val BASE_MTA_URL = "https://mta.maryland.gov"
        const val SERVICE_ALERT_PATH = "/service-alerts"
    }
}

data class BasicAlert(
    val title: String,
    val line: String,
    val dateTime: String,
    val path: String
)

sealed class AlertRepoState {
    object Init : AlertRepoState()
    object Error : AlertRepoState()
    data class Content(
        val alerts: List<BasicAlert>
    ) : AlertRepoState()
}

