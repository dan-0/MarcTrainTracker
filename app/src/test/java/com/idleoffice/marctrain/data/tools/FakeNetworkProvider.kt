package com.idleoffice.marctrain.data.tools

import com.idleoffice.marctrain.network.NetworkProvider

class FakeNetworkProvider : NetworkProvider {
    var isConnected = true
    override fun isNetworkConnected(): Boolean = isConnected
}