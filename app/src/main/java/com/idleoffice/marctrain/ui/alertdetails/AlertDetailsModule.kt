package com.idleoffice.marctrain.ui.alertdetails

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val alertDetailsModule = module {

    viewModel { AlertDetailsViewModel(get(), get()) }

    factory { AlertDetailsRepoImpl(get(), get()) as AlertDetailsRepo }

}