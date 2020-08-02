package com.idleoffice.marctrain.logging

import com.idleoffice.marctrain.MainApp
import timber.log.Timber

object Logging {
    fun init() {
        Timber.plant(MainApp.CrashlyticsTree())
    }
}