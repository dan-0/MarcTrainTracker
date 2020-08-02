/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * ScheduleViewModel.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import com.idleoffice.marctrain.analytics.FirebaseService
import com.idleoffice.marctrain.coroutines.CoroutineContextProvider
import com.idleoffice.marctrain.idling.IdlingResource
import com.idleoffice.marctrain.retrofit.ts.TrainScheduleService
import com.idleoffice.marctrain.ui.schedule.interactor.HapticEvent
import com.idleoffice.marctrain.ui.schedule.interactor.ScheduleAction
import com.idleoffice.marctrain.ui.schedule.interactor.ScheduleEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File


class ScheduleViewModel(
    private val dispatchers: CoroutineContextProvider,
    private val idlingResource: IdlingResource,
    private val trainScheduleService: TrainScheduleService,
    private val appFileDir: File,
    private val analyticService: FirebaseService
) : ViewModel() {

    companion object {
        const val lineBaseDir = "tables"
        private const val STATION_PENN = "penn"
        private const val STATION_CAMDEN = "camden"
        private const val STATION_BRUNSWICK = "brunswick"
    }

    private val _event = LiveEvent<ScheduleEvent>()
    val event: LiveData<ScheduleEvent> = _event

    private val _hapticEvent = MutableLiveData<HapticEvent>()
    val hapticEvent: LiveData<HapticEvent> = _hapticEvent

    private val lineTableExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable)
        _event.postValue(ScheduleEvent.Error(throwable))
    }

    private fun generateTempFile(tempFileName: String): File {
        val tablesDir = File(appFileDir, lineBaseDir)
        tablesDir.mkdirs()
        tablesDir.deleteOnExit()
        return File(tablesDir, tempFileName)
    }

    @Synchronized
    private suspend fun launchTable(lineName: String) {

        updateEvent(ScheduleEvent.Loading)

        viewModelScope.launch (dispatchers.io + lineTableExceptionHandler) {
            val scheduleResponse = trainScheduleService.getScheduleAsync(lineName).await()

            val destination = generateTempFile("${lineName}Schedule.pdf")

            scheduleResponse.byteStream().use { fis ->
                destination.outputStream().use { fos -> fis.copyTo(fos) }
            }

            val newEvent = ScheduleEvent.Data(destination, lineName)
            updateEvent(newEvent)
        }
    }

    fun takeAction(action: ScheduleAction) {
        idlingResource.startIdlingAction()
        _hapticEvent.value = HapticEvent.Tap()
        when (action) {
            ScheduleAction.LaunchBrunswick -> doLoadLineTable(STATION_BRUNSWICK)
            ScheduleAction.LaunchCamden -> doLoadLineTable(STATION_CAMDEN)
            ScheduleAction.LaunchPenn -> doLoadLineTable(STATION_PENN)
            ScheduleAction.LaunchLiveView -> updateEvent(ScheduleEvent.LoadLive)
        }
    }

    private fun updateEvent(newEvent: ScheduleEvent) {
        analyticService.newEvent(newEvent)
        _event.postValue(newEvent)
    }

    private fun doLoadLineTable(line: String) = viewModelScope.launch(dispatchers.io) {
        launchTable(line)
    }
}

