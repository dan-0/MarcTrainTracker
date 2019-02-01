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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.databinding.FragmentScheduleBinding
import com.idleoffice.marctrain.ui.base.BaseFragment
import com.idleoffice.marctrain.ui.main.OnBackPressedHandler
import com.idleoffice.marctrain.ui.main.OnBackPressedListener
import kotlinx.android.synthetic.main.fragment_schedule.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ScheduleFragment :
        BaseFragment<FragmentScheduleBinding, ScheduleViewModel>(), ScheduleNavigator, OnBackPressedListener {

    override val fragViewModel: ScheduleViewModel by viewModel()

    override val layoutId: Int = R.layout.fragment_schedule

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
        fragViewModel.navigator = this
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as OnBackPressedHandler).backButtonReceiver = this
        showLoading("Loading MARC schedules from MDOT")

        scheduleWebView.webViewClient = ScheduleClient(MTA_HOST, onPageFinishedListener)

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
        scheduleWebView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        scheduleWebView?.webViewClient = null
        (activity as OnBackPressedHandler).backButtonReceiver = null
        super.onDestroyView()
    }

    override fun goHome() {
        scheduleWebView.loadUrl(ScheduleFragment.MARC_SCHEDULE_URL)
    }
}