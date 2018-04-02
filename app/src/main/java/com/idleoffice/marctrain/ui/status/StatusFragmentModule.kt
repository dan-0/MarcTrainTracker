package com.idleoffice.marctrain.ui.status

import android.app.Application
import android.arch.lifecycle.ViewModelProvider
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.idleoffice.marctrain.ui.ViewModelProviderFactory
import dagger.Module
import dagger.Provides

@Module
class StatusFragmentModule {

    @Provides
    fun provideStatusViewModel(application: Application,
                               schedulerProvider: SchedulerProvider,
                               trainDataService: TrainDataService): StatusViewModel {
        return StatusViewModel(application, schedulerProvider, trainDataService)
    }

    @Provides
    fun statusViewModelProvider(statusViewModel: StatusViewModel) : ViewModelProvider.Factory {
        return ViewModelProviderFactory<Any>(statusViewModel)
    }

    @Provides
    fun provideStatusAdapter() : StatusAdapter {
        return StatusAdapter(mutableListOf())
    }
}