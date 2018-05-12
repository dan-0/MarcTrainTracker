package com.idleoffice.marctrain.ui.schedule

import android.app.Application
import android.content.res.AssetManager
import com.idleoffice.marctrain.helpers.vibrateTap
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.idleoffice.marctrain.ui.base.BaseViewModel
import io.reactivex.Single
import timber.log.Timber
import java.io.File


class ScheduleViewModel(val app: Application,
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
    private fun launchTable(fileName: String, saveFileName: String) {
        vibrateTap(app)

        val appAssets: AssetManager = navigator?.appAssets ?:
            throw NullNavigatorValueException("App AssetManager was null")

        val fis = appAssets.open(fileName)
        val destination = generateTempFile(saveFileName)
        val fos = destination.outputStream()

        Single.fromCallable({ fis.copyTo(fos) })
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe( {
                    navigator?.startActivity(destination)
                }, {
                    Timber.e(it)
                })
    }

    fun launchPennTable() {
        launchTable(lineBaseDir + File.pathSeparator + pennFileName, pennFileName)
    }

    fun launchCamdenTable() {
        launchTable(lineBaseDir + File.pathSeparator + camdenFileName, camdenFileName)
    }

    fun launchBrunswickTable() {
        launchTable(lineBaseDir + File.pathSeparator + brunswickFileName, brunswickFileName)
    }

    class NullNavigatorValueException(msg: String) : Exception(msg)
}

