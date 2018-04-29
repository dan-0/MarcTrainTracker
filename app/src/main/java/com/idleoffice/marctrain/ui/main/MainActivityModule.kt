package com.idleoffice.marctrain.ui.main

import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

val mainActivityModule : Module = applicationContext {
    viewModel { MainViewModel(get(), get()) }
}