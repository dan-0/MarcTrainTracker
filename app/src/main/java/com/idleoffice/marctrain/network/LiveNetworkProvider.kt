package com.idleoffice.marctrain.network

import android.content.Context
import android.net.ConnectivityManager

class LiveNetworkProvider(val context: Context): NetworkProvider {

    override fun isNetworkConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager? ?: return false

        val network = cm.activeNetworkInfo
        return network != null && network.isConnected
    }
}