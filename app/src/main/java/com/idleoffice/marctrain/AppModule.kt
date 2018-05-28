package com.idleoffice.marctrain

import android.content.Context
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.rx.AppSchedulerProvider
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

val appModules = applicationContext {
    bean { AppSchedulerProvider() as SchedulerProvider}
    bean {
        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        Retrofit.Builder()
                .baseUrl(BuildConfig.POLL_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(TrainDataService::class.java)
    }

    bean { this.androidApplication().getSharedPreferences("prefs", Context.MODE_PRIVATE)  }
}