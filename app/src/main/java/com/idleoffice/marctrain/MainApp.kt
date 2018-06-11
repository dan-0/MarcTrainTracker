/*
 * Copyright (c) 2018 IdleOffice Inc.
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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.idleoffice.marctrain

import android.app.Application
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.idleoffice.marctrain.ui.alert.alertFragmentModule
import com.idleoffice.marctrain.ui.main.mainActivityModule
import com.idleoffice.marctrain.ui.schedule.scheduleFragmentModule
import com.idleoffice.marctrain.ui.status.statusFragmentModule
import com.squareup.leakcanary.LeakCanary
import org.koin.android.ext.android.startKoin
import timber.log.Timber


class MainApp : Application() {

    private val koinModules = listOf(
            appModules,
            scheduleFragmentModule,
            mainActivityModule,
            statusFragmentModule,
            alertFragmentModule)

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)

        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }

        startKoin(this, koinModules)
    }

    class CrashlyticsTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            when(priority) {
                Log.ERROR -> {
                    if(t != null) {
                        Crashlytics.logException(t)
                    }
                    Crashlytics.log(priority, tag, message)
                }
            }
        }
    }
}