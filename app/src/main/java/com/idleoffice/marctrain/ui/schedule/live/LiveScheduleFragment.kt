/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * LiveScheduleFragment.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain.ui.schedule.live

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.idleoffice.marctrain.databinding.FragmentLiveScheduleBinding
import com.idleoffice.marctrain.ui.main.OnBackPressedHandler
import com.idleoffice.marctrain.ui.main.OnBackPressedListener
import com.idleoffice.marctrain.ui.schedule.ScheduleClient
import kotlinx.android.synthetic.main.fragment_live_schedule.*
import kotlinx.android.synthetic.main.progress_bar_frame_layout_full.*
import timber.log.Timber

class LiveScheduleFragment : Fragment(), OnBackPressedListener {

    companion object {
        private const val MTA_HOST = "www.mta.maryland.gov"
        const val MARC_SCHEDULE_URL = "https://$MTA_HOST/schedule?type=marc-train"
    }

    private val onPageFinishedListener = object: ScheduleClient.OnPageFinishedListener {
        override fun pageFinishedLoading(url: String?) {
            hideLoading()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentLiveScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as OnBackPressedHandler).backButtonReceiver = this
        showLoading("Loading MARC schedules from MDOT")

        scheduleWebView.webViewClient = ScheduleClient(MTA_HOST, onPageFinishedListener)

        @SuppressLint("SetJavaScriptEnabled")
        scheduleWebView.settings.javaScriptEnabled = true


        if (savedInstanceState !== null) {
            scheduleWebView.restoreState(savedInstanceState)
        } else {
            scheduleWebView.loadUrl(MARC_SCHEDULE_URL)
        }

        scheduleWebView.visibility = View.INVISIBLE

        scheduleWebView.setDownloadListener { url, _, _, _, _ ->
            val actionIntent = Intent(Intent.ACTION_VIEW)
            actionIntent.data = (Uri.parse(url))
            startActivity(actionIntent)
        }

        goHome()

        scheduleFab.setOnClickListener { goHome() }
    }

    override fun backButtonPressed(): Boolean {
        scheduleWebView ?: return false
        if (scheduleWebView.canGoBack()) {
            scheduleWebView.goBack()
            return true
        }
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        scheduleWebView?.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        scheduleWebView?.webViewClient = null
        (activity as OnBackPressedHandler).backButtonReceiver = null
        super.onDestroyView()
    }

    private fun goHome() {
        scheduleWebView?.loadUrl(MARC_SCHEDULE_URL)
    }

    private fun showLoading(msg: String) {
        Timber.d("Showing loading view.")
        loadingTextViewFull?.text = msg
        loadingViewFull?.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        Timber.d("Hiding loading view.")
        loadingTextViewFull?.text = ""
        loadingViewFull?.visibility = View.GONE
    }
}
