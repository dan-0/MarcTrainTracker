package com.idleoffice.marctrain.ui.schedule

import android.app.Application
import android.arch.lifecycle.ViewModelProvider
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.idleoffice.marctrain.ui.ViewModelProviderFactory
import dagger.Module
import dagger.Provides

@Module
class ScheduleFragmentModule {

    @Provides
    fun provideScheduleViewModel(app: Application,
                                 schedulerProvider: SchedulerProvider) :ScheduleViewModel {
        return ScheduleViewModel(app, schedulerProvider)
    }

    @Provides
    fun scheduleViewModelProvider(scheduleViewModel: ScheduleViewModel) : ViewModelProvider.Factory {
        return ViewModelProviderFactory<Any>(scheduleViewModel)
    }
}