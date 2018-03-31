package com.idleoffice.marctrain.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider


class ViewModelProviderFactory<V : Any>(private var viewModel : V) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(viewModel::class.java)) {
            return viewModel as T
        }

        throw IllegalArgumentException("Classname not known")
    }
}

