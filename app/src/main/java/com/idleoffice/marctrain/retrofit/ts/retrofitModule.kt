/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * retrofitModule.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain.retrofit.ts

import com.idleoffice.marctrain.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val retrofitModule = module {
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
}