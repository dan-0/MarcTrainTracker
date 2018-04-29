package com.idleoffice.marctrain.ui.alert

import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

val alertFragmentModule : Module = applicationContext {
    viewModel { AlertViewModel(get(), get(), get()) }
    bean { AlertAdapter(mutableListOf()) }
}