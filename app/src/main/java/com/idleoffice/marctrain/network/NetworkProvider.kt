package com.idleoffice.marctrain.network

interface NetworkProvider {
    fun isNetworkConnected(): Boolean
}