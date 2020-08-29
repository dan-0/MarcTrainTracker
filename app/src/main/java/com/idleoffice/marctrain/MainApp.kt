/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * MainApp.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain

import android.app.Application
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.idleoffice.marctrain.analytics.firebaseModule
import com.idleoffice.marctrain.coroutines.coroutinesModule
import com.idleoffice.marctrain.idling.idlingResourceModule
import com.idleoffice.marctrain.logging.KoinLogger
import com.idleoffice.marctrain.logging.Logging
import com.idleoffice.marctrain.retrofit.ts.retrofitModule
import com.idleoffice.marctrain.ui.alert.alertFragmentModule
import com.idleoffice.marctrain.ui.alertdetails.alertDetailsModule
import com.idleoffice.marctrain.ui.schedule.scheduleFragmentModule
import com.idleoffice.marctrain.ui.status.statusFragmentModule
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.android.startKoin
import timber.log.Timber


class MainApp : Application() {

    private val koinModules = listOf(
        appModules,
        scheduleFragmentModule,
        statusFragmentModule,
        alertFragmentModule,
        idlingResourceModule,
        firebaseModule,
        coroutinesModule,
        retrofitModule,
        alertDetailsModule
    )

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)

        Logging.init()

        val koinLogger = KoinLogger()

        startKoin(this, koinModules, logger = koinLogger)
    }

    class CrashlyticsTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            val crashlytics = FirebaseCrashlytics.getInstance()
            when (priority) {
                Log.ERROR -> {
                    if (t != null) {
                        crashlytics.recordException(t)
                    }
                    crashlytics.log("E/$tag: $message")
                }
            }
        }
    }
}