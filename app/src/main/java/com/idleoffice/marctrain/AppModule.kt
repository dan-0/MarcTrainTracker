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
import com.idleoffice.marctrain.network.LiveNetworkProvider
import com.idleoffice.marctrain.network.NetworkProvider
import com.idleoffice.marctrain.okhttp.getContent
import com.idleoffice.marctrain.util.StringProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module
import timber.log.Timber
import java.util.concurrent.TimeUnit

val appModules = module {

    factory {
        Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
    }

    single {
        OkHttpClient.Builder()
                .addInterceptor {
                    // Loop the try block, erring out due to an io issue is not a real option
                    while (true) {
                        try {
                            val response = it.proceed(it.request())
                            val body = response.body()
                            val content = it.getContent(body)
                            val contentType = response.body()?.contentType()
                            response.body()?.close()
                            return@addInterceptor response.newBuilder().body(ResponseBody.create(contentType, content)).build()
                        } catch (exception: Exception) {
                            Timber.e(exception, "Error parsing call chain")
                        }
                    }
                    // Suppress, this case will never happen, but if it isn't here the compiler vomits
                    @Suppress("UNREACHABLE_CODE")
                    Response.Builder().build()
                }
                .retryOnConnectionFailure(true)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
    }

    single { androidApplication().getSharedPreferences("prefs", Context.MODE_PRIVATE) }
    single { LiveNetworkProvider(get()) as NetworkProvider }

    single { StringProvider(get()) }
}