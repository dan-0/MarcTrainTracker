package com.idleoffice.marctrain.ui.main

import android.app.Application
import android.arch.lifecycle.ViewModelProvider
import com.idleoffice.marctrain.retrofit.tss.TrainStatusService
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.idleoffice.marctrain.ui.ViewModelProviderFactory
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {

    @Provides
    fun provideMainViewModel(
            application: Application,
            schedulerProvider: SchedulerProvider) : MainViewModel {
        return MainViewModel(application, schedulerProvider)
    }

    @Provides
    fun mainViewModelProvider(mainViewModel: MainViewModel) : ViewModelProvider.Factory {
        return ViewModelProviderFactory<Any>(mainViewModel)
    }
}