package com.idleoffice.marctrain.di

import com.idleoffice.marctrain.rx.AppSchedulerProvider
import com.idleoffice.marctrain.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    fun provideSchedulerProvider(): SchedulerProvider {
        return AppSchedulerProvider()
    }
}