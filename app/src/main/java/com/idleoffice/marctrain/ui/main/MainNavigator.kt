package com.idleoffice.marctrain.ui.main


interface MainNavigator {
    fun displayError(errorMsg : String)
    fun showLoading()
    fun hideLoading()
}