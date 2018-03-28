package com.idleoffice.marctrain.ui.status

import android.app.Application
import android.arch.lifecycle.ViewModelProvider
import com.idleoffice.marctrain.retrofit.tss.TrainStatusService
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.idleoffice.marctrain.ui.ViewModelProviderFactory
import dagger.Module
import dagger.Provides

@Module
class StatusFragmentModule {

    @Provides
    fun provideStatusViewModel(application: Application,
                               schedulerProvider: SchedulerProvider,
                               trainStatusService: TrainStatusService): StatusViewModel {
        return StatusViewModel(application, schedulerProvider, trainStatusService)
    }

    @Provides
    fun statusViewModelProvider(statusViewModel: StatusViewModel) : ViewModelProvider.Factory {
        return ViewModelProviderFactory<Any>(statusViewModel)
    }
}