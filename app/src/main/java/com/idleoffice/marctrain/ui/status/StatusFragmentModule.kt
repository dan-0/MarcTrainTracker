package com.idleoffice.marctrain.ui.status

import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

val statusFragmentModule : Module = applicationContext {
    viewModel { StatusViewModel(get(), get(), get()) }
    bean { StatusAdapter(mutableListOf()) }
}