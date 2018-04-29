package com.idleoffice.marctrain.ui.schedule

import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

val scheduleFragmentModule : Module = applicationContext {
    viewModel { ScheduleViewModel(get(), get()) }
}