/*
 * Copyright (c) 2019 IdleOffice Inc.
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
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.idleoffice.marctrain.ui.schedule

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.idleoffice.marctrain.BR
import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.databinding.FragmentScheduleBinding
import com.idleoffice.marctrain.extensions.exhaustive
import com.idleoffice.marctrain.idling.IdlingResource
import com.idleoffice.marctrain.ui.base.BaseFragment
import com.idleoffice.marctrain.ui.schedule.interactor.HapticEvent
import com.idleoffice.marctrain.ui.schedule.interactor.ScheduleActor
import com.idleoffice.marctrain.ui.schedule.interactor.ScheduleEvent
import com.idleoffice.marctrain.ui.schedule.live.LiveScheduleFragment
import com.idleoffice.marctrain.vibrateTap
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.progress_bar_frame_layout_full.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File

class ScheduleFragment :
        BaseFragment<FragmentScheduleBinding, ScheduleViewModel>() {

    override val fragViewModel: ScheduleViewModel by viewModel()

    private val idlingResource: IdlingResource by inject()

    override val layoutId: Int = R.layout.fragment_schedule

    private lateinit var binding: FragmentScheduleBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentScheduleBinding.inflate(inflater, container, false)
        rootView = binding.root
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setVariable(BR.actor, ScheduleActor(fragViewModel))
        observeData()
    }

    private fun observeData() {
        val eventObserver = Observer<ScheduleEvent?> @Synchronized {
            it?.run { analyticService.newEvent(it) }
            when (it) {
                is ScheduleEvent.Loading -> showLoading(getString(R.string.loading_schedule))
                is ScheduleEvent.Error -> {
                    hideLoading()
                    idlingResource.stopIdlingAction()
                    unableToGetPdf()
                    Timber.e(it.e, "Error getting PDF: ${it.e.message}")
                }
                is ScheduleEvent.Data -> {
                    idlingResource.stopIdlingAction()
                    startPdfActivity(it.file)
                    hideLoading()
                }
                ScheduleEvent.LoadLive -> {
                    idlingResource.stopIdlingAction()
                    fragmentManager?.let { fm ->
                        fm.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.view_content, LiveScheduleFragment())
                            .commit()
                    }
                }
                else -> {
                    idlingResource.stopIdlingAction()
                    Timber.d("Null ScheduleEvent")
                }
            }
        }

        val hapticObserver = Observer<HapticEvent?> {
            it?.let { context?.vibrateTap() }
        }

        fragViewModel.event.observe(viewLifecycleOwner, eventObserver)
        fragViewModel.hapticEvent.observe(viewLifecycleOwner, hapticObserver)
    }

    private fun startPdfActivity(destination: File) {
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

    private fun unableToGetPdf() {
        view?.let {
            Toast.makeText(it.context, "Schedule unavailable, check network", Toast.LENGTH_LONG).show()
        }
    }

    private fun displayActivityNotFound() {
        view?.let {
            Snackbar.make(it, R.string.pdf_reader_unavailable, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun showLoading(msg: String) {
        Timber.d("Showing loading view.")
        setButtonsClickable(false)
        activity?.run {
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            loadingTextViewFull?.text = msg
            loadingViewFull?.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        setButtonsClickable(true)
        activity?.run {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            loadingTextViewFull?.text = ""
            loadingViewFull?.visibility = View.GONE
        }

    }

    private fun setButtonsClickable(isClickable: Boolean) {
        btnTablesPenn.isClickable = isClickable
        btnTablesCamden.isClickable = isClickable
        btnTablesBrunswick.isClickable = isClickable
    }
}