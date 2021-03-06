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
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.databinding.FragmentScheduleBinding
import com.idleoffice.marctrain.idling.IdlingResource
import com.idleoffice.marctrain.ui.schedule.interactor.ScheduleActor
import com.idleoffice.marctrain.ui.schedule.interactor.ScheduleEvent
import com.idleoffice.marctrain.vibrateTap
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File

class ScheduleFragment : Fragment() {

    private val viewModel: ScheduleViewModel by viewModel()

    private val idlingResource: IdlingResource by inject()

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindActor()
        observeData()
    }

    private fun observeData() {

        viewModel.event.observe(viewLifecycleOwner) {
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
                    val direction = ScheduleFragmentDirections.toLiveScheduleFragment()
                    findNavController().navigate(direction)
                }
            }
        }


        viewModel.hapticEvent.observe(viewLifecycleOwner) {
            it.let { requireContext().vibrateTap() }
        }
    }

    private fun startPdfActivity(destination: File) {
        requireContext().let {
            val fileUri = FileProvider.getUriForFile(it,
                    "${BuildConfig.APPLICATION_ID}.fileprovider", destination)
            val pdfIntent = Intent(Intent.ACTION_VIEW)

            pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION
            val mimeType = activity?.contentResolver?.getType(fileUri) ?: return

            pdfIntent.setDataAndType(fileUri, mimeType)

            try {
                startActivity(pdfIntent)
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

        binding.loadingLayout.loadingTextViewFull.text = msg
        binding.loadingLayout.loadingViewFull.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        setButtonsClickable(true)
        binding.loadingLayout.loadingTextViewFull?.text = ""
        binding.loadingLayout.loadingViewFull?.visibility = View.GONE

    }

    private fun setButtonsClickable(isClickable: Boolean) {
        binding.btnTablesPenn.isClickable = isClickable
        binding.btnTablesCamden.isClickable = isClickable
        binding.btnTablesBrunswick.isClickable = isClickable
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindActor() {
        val actor = ScheduleActor(viewModel::takeAction)

        with(binding) {
            btnTablesPenn.setOnClickListener {
                actor.launchPennTable()
            }

            btnTablesCamden.setOnClickListener {
                actor.launchCamdenTable()
            }

            btnTablesBrunswick.setOnClickListener {
                actor.launchBrunswickTable()
            }

            btnMdotSchedule.setOnClickListener {
                actor.launchLiveMode()
            }
        }
    }
}