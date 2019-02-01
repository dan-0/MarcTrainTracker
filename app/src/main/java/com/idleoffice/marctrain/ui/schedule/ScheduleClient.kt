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