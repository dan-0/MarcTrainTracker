package com.idleoffice.marctrain.ui.status

interface StatusNavigator {
    fun showLoading(msg: String)
    fun hideLoading()
}