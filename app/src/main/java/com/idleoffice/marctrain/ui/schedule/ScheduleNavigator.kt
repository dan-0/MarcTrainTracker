package com.idleoffice.marctrain.ui.schedule

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import java.io.File

interface ScheduleNavigator {
    fun startActivity(destination: File)
    var appFilesDir : File?
    var appAssets : AssetManager?
}