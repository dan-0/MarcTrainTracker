/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * ScheduleClient.kt is part of MarcTrainTracker.
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

import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.idleoffice.marctrain.R

class ScheduleClient(
        private val authorizedHost: String,
        private val onPageFinishedListener: OnPageFinishedListener
): WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.visibility = View.VISIBLE

        onPageFinishedListener.pageFinishedLoading(url)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

        return if (isValidRequest(request)) {
            super.shouldOverrideUrlLoading(view, request)
        } else {
            view?.context ?: return true
            Toast.makeText(view.context, view.context.getString(R.string.invalid_request), Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun isValidRequest(request: WebResourceRequest?): Boolean {
        request ?: return false

        val host = request.url.host

        if (host.isNullOrEmpty()) {
            return false
        }

        val path = request.url?.path

        if ((host.endsWith(authorizedHost) && path?.contains("schedule") == true)
                || request.url.toString().endsWith(".pdf")) {
            return true
        }

        return false
    }

    interface OnPageFinishedListener {
        fun pageFinishedLoading(url: String?)
    }
}