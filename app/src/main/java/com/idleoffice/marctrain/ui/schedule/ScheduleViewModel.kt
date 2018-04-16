package com.idleoffice.marctrain.ui.schedule

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.support.v4.content.FileProvider
import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.helpers.vibrateTap
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.idleoffice.marctrain.ui.base.BaseViewModel
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream


class ScheduleViewModel(app: Application,
                        schedulerProvider: SchedulerProvider) :
        BaseViewModel<ScheduleNavigator>(app, schedulerProvider){

    init {
        Timber.d("Initialized...")
    }

    private fun launchTable(fileName: String, saveFileName: String) {
        if (navigator == null) {
            return
        }

        val tablesDir = File(navigator!!.appFilesDir, "tables")
        tablesDir.mkdirs()
        tablesDir.deleteOnExit()
        val tempFile = File(tablesDir, saveFileName)
        val fos = FileOutputStream(tempFile)
        val fullTable = BufferedInputStream(navigator!!.appAssets?.open(fileName))
        val buffer = ByteArray(2048)

        fullTable.use { input ->
            fos.use {
                while (true) {
                    val len = input.read(buffer)
                    if (len <= 0) {
                        break
                    }
                    it.write(buffer, 0, len)
                }
                it.flush()
            }
        }

        val fileUri = FileProvider.getUriForFile(navigator!!.appContext!!,
                "${BuildConfig.APPLICATION_ID}.fileprovider", tempFile)
        val pdfIntent = Intent(Intent.ACTION_VIEW)

        pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION
        val mimeType = navigator!!.appContentResolver!!.getType(fileUri)
        pdfIntent.setDataAndType(fileUri, mimeType)

        try {
            navigator?.startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            navigator?.displayActivityNotFound()
            Timber.w("Activity not found: ${e.message}")
        }
    }

    fun launchPennTable() {
        vibrateTap(navigator?.appContext!!)
        launchTable("tables/pennFull.pdf", "PENN LINE.pdf")
    }

    fun launchCamdenTable() {
        vibrateTap(navigator?.appContext!!)
        launchTable("tables/camdenFull.pdf", "CAMDEN LINE.pdf")
    }

    fun launchBrunswickTable() {
        vibrateTap(navigator?.appContext!!)
        launchTable("tables/brunswickFull.pdf", "BRUNSWICK LINE.pdf")
    }
}

