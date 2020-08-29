package com.idleoffice.marctrain.util.toplevel

import timber.log.Timber

inline fun logWIllegalArgument(msg: String) {
    val e = IllegalArgumentException(msg)
    Timber.w(e)
}

inline fun logEIllegalArgument(msg: String) {
    val e = IllegalArgumentException(msg)
    Timber.e(e)
}