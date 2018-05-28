package com.idleoffice.marctrain

import android.app.Application
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.idleoffice.marctrain.ui.alert.alertFragmentModule
import com.idleoffice.marctrain.ui.main.mainActivityModule
import com.idleoffice.marctrain.ui.schedule.scheduleFragmentModule
import com.idleoffice.marctrain.ui.status.statusFragmentModule
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