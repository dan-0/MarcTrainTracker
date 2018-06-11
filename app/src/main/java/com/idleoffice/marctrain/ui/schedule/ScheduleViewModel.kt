/*
 * Copyright (c) 2018 IdleOffice Inc.
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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.idleoffice.marctrain.ui.schedule

import android.app.Application
import android.content.res.AssetManager
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.idleoffice.marctrain.ui.base.BaseViewModel
import io.reactivex.Single
import timber.log.Timber
import java.io.File


class ScheduleViewModel(app: Application,
                        schedulerProvider: SchedulerProvider) :
        BaseViewModel<ScheduleNavigator>(app, schedulerProvider) {

    companion object {
        const val lineBaseDir = "tables"
        const val pennFileName = "pennFull.pdf"
        const val camdenFileName = "camdenFull.pdf"
        const val brunswickFileName = "brunswickFull.pdf"
    }

    init {
        Timber.d("Initialized...")
    }

    fun generateTempFile(tempFileName: String) : File {
        val appFileDir: File = navigator?.appFilesDir ?:
                throw NullNavigatorValueException("App file directory was null")
        val tablesDir = File(appFileDir, lineBaseDir)
        tablesDir.mkdirs()
        tablesDir.deleteOnExit()
        return File(tablesDir, tempFileName)
    }


    @Synchronized
    private fun launchTable(fileName: String) {

        val filePath = "$lineBaseDir${File.separator}$fileName"

        navigator?.vibrateTap()

        // We can silently fail, if these are null its because this ViewModel is not attached to an activity
        val appAssets: AssetManager = navigator?.appAssets ?: return

        val fis = appAssets.open(filePath)
        val destination = try {
             generateTempFile(fileName)
        } catch (e: NullNavigatorValueException) {
            Timber.w(e, "Error generating temp file.")
            return
        }

        val fos = destination.outputStream()

        Single.fromCallable({ fis.copyTo(fos) })
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe( {
                    navigator?.startPdfActivity(destination)
                }, {
                    Timber.e(it)
                })
    }

    fun launchPennTable() {
        launchTable(pennFileName)
    }

    fun launchCamdenTable() {
        launchTable(camdenFileName)
    }

    fun launchBrunswickTable() {
        launchTable(brunswickFileName)
    }

    class NullNavigatorValueException(msg: String) : Exception(msg)
}

