package com.idleoffice.marctrain.ui.main

import android.arch.lifecycle.ViewModelProvider
import com.idleoffice.marctrain.ui.ViewModelProviderFactory
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {
    @Provides
    fun mainViewModelProvider(mainViewModel: MainViewModel) : ViewModelProvider.Factory {
        return ViewModelProviderFactory<Any>(mainViewModel)
    }
}