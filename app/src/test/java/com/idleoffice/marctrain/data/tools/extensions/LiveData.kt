package com.idleoffice.marctrain.data.tools.extensions

import androidx.lifecycle.LiveData

fun <T> LiveData<T>.toLiveList(): List<T> {
    val states = mutableListOf<T>()
    observeForever {
        states.add(it)
    }

    return states
}