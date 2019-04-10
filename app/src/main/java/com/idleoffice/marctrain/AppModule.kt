/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * AppModule.kt is part of MarcTrainTracker.
 *
 * MarcTrainTracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MarcTrainTracker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.idleoffice.marctrain

import android.content.Context
import com.idleoffice.marctrain.coroutines.AppCoroutineContextProvider
import com.idleoffice.marctrain.coroutines.CoroutineContextProvider
import com.idleoffice.marctrain.network.LiveNetworkProvider
import com.idleoffice.marctrain.network.NetworkProvider
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.retrofit.ts.TrainScheduleService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

val appModules = module {
    single { AppCoroutineContextProvider() as CoroutineContextProvider }

    factory {
        Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
    }

    factory {
        OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .eventListenerFactory {
                    return@eventListenerFactory object : EventListener() {
                        init {
                            Timber.d("Request: ${it.request().url()}")
                        }

                        override fun callFailed(call: Call, ioe: IOException) {
                            super.callFailed(call, ioe)
                            Timber.d("Request: ${call.request().url()}")
                        }

                        override fun requestBodyEnd(call: Call, byteCount: Long) {
                            Timber.d("Request: ${call.request().url()}")
                            super.requestBodyEnd(call, byteCount)
                        }
                    }
                }
                .build()
    }

    single {
        Retrofit.Builder()
                .baseUrl(BuildConfig.POLL_URL)
                .addConverterFactory(MoshiConverterFactory.create(get()).asLenient())
                .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
                .client(get())
                .build()
                .create(TrainDataService::class.java)
    }

    factory {
        Retrofit.Builder()
                .baseUrl("https://marctrain.app")
                .addConverterFactory(MoshiConverterFactory.create(get()).asLenient())
                .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
                .client(get())
                .build()
                .create(TrainScheduleService::class.java)
    }

    single { androidApplication().getSharedPreferences("prefs", Context.MODE_PRIVATE) }
    single { LiveNetworkProvider(get()) as NetworkProvider }
}