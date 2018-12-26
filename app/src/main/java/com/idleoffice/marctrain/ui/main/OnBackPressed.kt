package com.idleoffice.marctrain.ui.main

interface OnBackPressedHandler {
    var backButtonReceiver: OnBackPressedListener?
}

interface OnBackPressedListener {
    fun backButtonPressed(): Boolean
}