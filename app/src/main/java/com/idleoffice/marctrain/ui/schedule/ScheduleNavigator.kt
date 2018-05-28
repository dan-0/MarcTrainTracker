package com.idleoffice.marctrain.ui.schedule

import android.content.res.AssetManager
import java.io.File

interface ScheduleNavigator {
    fun startPdfActivity(destination: File)
    var appFilesDir : File?
    var appAssets : AssetManager?
    fun vibrateTap()
}