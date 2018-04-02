package com.idleoffice.marctrain.ui.alert

import android.app.Application
import android.arch.lifecycle.ViewModelProvider
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.idleoffice.marctrain.ui.ViewModelProviderFactory
import dagger.Module
import dagger.Provides

@Module
class AlertFragmentModule {

    @Provides
    fun provideAlertViewModel(app: Application,
                              schedulerProvider: SchedulerProvider,
                              trainDataService: TrainDataService): AlertViewModel {
        return AlertViewModel(app, schedulerProvider, trainDataService)
    }

    @Provides
    fun alertViewModelProvider(alertViewModel: AlertViewModel) : ViewModelProvider.Factory {
        return ViewModelProviderFactory<Any>(alertViewModel)
    }

    @Provides
    fun provideAlertAdapter() : AlertAdapter {
        return AlertAdapter(mutableListOf())
    }
}