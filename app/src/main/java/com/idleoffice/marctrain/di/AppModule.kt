package com.idleoffice.marctrain.di

import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.retrofit.tss.TrainStatusService
import com.idleoffice.marctrain.rx.AppSchedulerProvider
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun provideSchedulerProvider(): SchedulerProvider {
        return AppSchedulerProvider()
    }

    @Provides
    @Singleton
    fun provideTrainStatusService() : TrainStatusService {
        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        return Retrofit.Builder()
                .baseUrl(BuildConfig.POLL_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(TrainStatusService::class.java)
    }
}