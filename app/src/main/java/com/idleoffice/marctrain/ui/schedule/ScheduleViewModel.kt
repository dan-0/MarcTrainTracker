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
import com.idleoffice.marctrain.coroutines.CoroutineContextProvider
import com.idleoffice.marctrain.extensions.exhaustive
import com.idleoffice.marctrain.idling.IdlingResource
import com.idleoffice.marctrain.retrofit.ts.TrainScheduleService
import com.idleoffice.marctrain.ui.base.BaseViewModel
import com.idleoffice.marctrain.ui.schedule.interactor.HapticEvent
import com.idleoffice.marctrain.ui.schedule.interactor.ScheduleAction
import com.idleoffice.marctrain.ui.schedule.interactor.ScheduleEvent
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File


class ScheduleViewModel(
        coroutineContextProvider: CoroutineContextProvider,
        private val idlingResource: IdlingResource,
        private val trainScheduleService: TrainScheduleService,
        private val appFileDir: File
) : BaseViewModel(coroutineContextProvider) {

    companion object {
        const val lineBaseDir = "tables"
        private const val STATION_PENN = "penn"
        private const val STATION_CAMDEN = "camden"
        private const val STATION_BRUNSWICK = "brunswick"
    }

    private val _event = MutableLiveData<ScheduleEvent>()
    val event: LiveData<ScheduleEvent> = _event

    private val _hapticEvent = MutableLiveData<HapticEvent>()
    val hapticEvent: LiveData<HapticEvent> = _hapticEvent

    init {
        Timber.d("Initialized...")
    }

    private fun generateTempFile(tempFileName: String): File {
        val tablesDir = File(appFileDir, lineBaseDir)
        tablesDir.mkdirs()
        tablesDir.deleteOnExit()
        return File(tablesDir, tempFileName)
    }

    @Synchronized
    private suspend fun launchTable(lineName: String) {

        _event.postValue(ScheduleEvent.Loading)

        val event: Deferred<ScheduleEvent> = ioScope.async {
            val scheduleResponse = trainScheduleService.getScheduleAsync(lineName).await()

            val destination = generateTempFile("${lineName}Schedule.pdf")

            scheduleResponse.byteStream().use { fis ->
                destination.outputStream().use { fos -> fis.copyTo(fos) }
            }

            ScheduleEvent.Data(destination, lineName)
        }

        _event.postValue(
                runCatching {
                    event.await()
                }.getOrElse {
                    ScheduleEvent.Error(it)
                }
        )
    }

    fun takeAction(action: ScheduleAction) {
        idlingResource.startIdlingAction()
        _hapticEvent.value = HapticEvent.Tap()
        val line = when (action) {
            is ScheduleAction.LaunchBrunswick -> STATION_BRUNSWICK
            is ScheduleAction.LaunchCamden -> STATION_CAMDEN
            is ScheduleAction.LaunchPenn -> STATION_PENN
        }.exhaustive

        ioScope.launch { launchTable(line) }
    }
}

