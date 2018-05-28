package com.idleoffice.marctrain.ui.schedule

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.databinding.FragmentScheduleBinding
import com.idleoffice.marctrain.ui.base.BaseFragment
import com.idleoffice.marctrain.vibrateTap
import org.koin.android.architecture.ext.viewModel
import timber.log.Timber
import java.io.File

class ScheduleFragment :
        BaseFragment<FragmentScheduleBinding, ScheduleViewModel>(), ScheduleNavigator {

    override val fragViewModel: ScheduleViewModel by viewModel()
    override var appFilesDir : File? = null
    override var appAssets : AssetManager? = null


    override val layoutId: Int = R.layout.fragment_schedule

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragViewModel.navigator = this
        appFilesDir = context?.filesDir
        appAssets = context?.assets
        retainInstance = true
    }

    private fun displayActivityNotFound() {
        view?.let {
            Snackbar.make(it, R.string.pdf_reader_unavailable, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun startPdfActivity(destination: File) {
        context?.let {
            val fileUri = FileProvider.getUriForFile(it,
                    "${BuildConfig.APPLICATION_ID}.fileprovider", destination)
            val pdfIntent = Intent(Intent.ACTION_VIEW)

            pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION
            val mimeType = activity?.contentResolver?.getType(fileUri) ?: return

            pdfIntent.setDataAndType(fileUri, mimeType)

            try {
                super.startActivity(pdfIntent)
            } catch (e: ActivityNotFoundException) {
                Timber.e(e)
                displayActivityNotFound()
            }
        }

    }

    override fun vibrateTap() {
        context?.vibrateTap()
    }
}