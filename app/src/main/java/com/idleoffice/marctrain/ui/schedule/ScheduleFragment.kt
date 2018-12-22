/*
 * Copyright (c) 2018 IdleOffice Inc.
 *
 * ScheduleFragment.kt is part of MarcTrainTracker.
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

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.databinding.FragmentScheduleBinding
import com.idleoffice.marctrain.ui.base.BaseFragment
import com.idleoffice.marctrain.vibrateTap
import org.koin.androidx.viewmodel.ext.android.viewModel
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