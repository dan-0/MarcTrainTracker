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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idleoffice.marctrain.coroutines.CoroutineContextProvider
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.data.tools.Direction
import com.idleoffice.marctrain.data.tools.TrainLine
import com.idleoffice.marctrain.data.tools.TrainStatusComparator
import com.idleoffice.marctrain.idling.IdlingResource
import com.idleoffice.marctrain.network.NetworkProvider
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.ui.status.data.StatusViewState
import com.idleoffice.marctrain.ui.status.data.TrainLineState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.threeten.bp.Duration
import timber.log.Timber

class StatusViewModel(
    private val dispatchers: CoroutineContextProvider,
    private val trainDataService: TrainDataService,
    private val networkProvider: NetworkProvider,
    private val idlingResource: IdlingResource
) : ViewModel() {

    private val _state = MutableLiveData<StatusViewState>(StatusViewState.Init())
    val state: LiveData<StatusViewState> = _state

    fun loadTrainStatus() = doGetTrainStatus()

    private suspend fun loadTrainData() {

        val trains = runCatching {
            trainDataService.getTrainStatus()
        }.getOrElse {
            Timber.e(it, "Error getting train status")
            return
        }

        val currentLineState = currentState().trainLineState

        val filteredTrains = filterTrains(trains, currentLineState)

        val newState = StatusViewState.Content(
            trains,
            filteredTrains,
            currentLineState
        )

        updateState(newState)
    }

    private fun doGetTrainStatus() {
        viewModelScope.launch(dispatchers.io) {
            while (true) {
                idlingResource.startIdlingAction()
                val delayInterval = if (networkProvider.isNetworkConnected()) {
                    STATUS_POLL_INTERVAL.toMillis()
                } else {
                    STATUS_POLL_RETRY_INTERVAL.toMillis()
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

    fun updateLine(linePosition: Int) {
        val current = currentState()
        val currentLineState = current.trainLineState

        val newLine = TrainLine.values().first { it.position == linePosition }

        if (currentLineState.line == newLine) {
            return
        }

        val newLineState = currentLineState.copy(
            line = newLine
        )

        val newState = when (current) {
            is StatusViewState.Init -> current.copy(trainLineState = newLineState)
            is StatusViewState.Content -> current.copy(
                filteredTrains = filterTrains(current.allTrains, newLineState),
                trainLineState = newLineState
            )
        }

        updateState(newState)
    }

    fun updateDirection(directionPosition: Int) {
        val current = currentState()
        val currentLineState = current.trainLineState

        val newDirection = Direction.values().first { it.position == directionPosition }

        if (currentLineState.direction == newDirection) {
            return
        }

        val newLineState = currentLineState.copy(
            direction = newDirection
        )

        val newState = when (current) {
            is StatusViewState.Init -> current.copy(trainLineState = newLineState)
            is StatusViewState.Content -> current.copy(
                filteredTrains = filterTrains(current.allTrains, newLineState),
                trainLineState = newLineState
            )
        }

        updateState(newState)
    }

    private fun filterTrains(
        trains: List<TrainStatus>,
        currentLineState: TrainLineState
    ): List<TrainStatus> {

        val lineString = currentLineState.line.lineName
        val directionString = when (currentLineState.direction) {
            Direction.FROM_DC -> currentLineState.line.fromDcDirectionFilterName
            Direction.TO_DC -> currentLineState.line.toDcDirectionFilterName
        }

        val stationNames = currentLineState.line.stations.let {
            when (currentLineState.direction) {
                Direction.TO_DC -> it.asReversed()
                else -> it
            }
        }

        return trains.filter {
            it.direction.equals(directionString, true) && it.line.equals(lineString, true)
        }.sortedWith(
            TrainStatusComparator(stationNames)
        )
    }

    private fun currentState() = _state.value!!

    private fun updateState(newState: StatusViewState) {
        Timber.d("new state: $newState")
        _state.postValue(newState)
    }

    companion object {
        private val STATUS_POLL_INTERVAL = Duration.ofMinutes(1)
        private val STATUS_POLL_RETRY_INTERVAL = Duration.ofSeconds(10)
    }
}

