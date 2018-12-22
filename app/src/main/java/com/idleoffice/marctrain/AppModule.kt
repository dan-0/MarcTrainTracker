/*
 * Copyright (c) 2018 IdleOffice Inc.
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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.idleoffice.marctrain

import android.content.Context
import com.idleoffice.marctrain.retrofit.ts.TrainDataService
import com.idleoffice.marctrain.coroutines.AppContextProvider
import com.idleoffice.marctrain.coroutines.ContextProvider
import com.idleoffice.marctrain.network.LiveNetworkProvider
import com.idleoffice.marctrain.network.NetworkProvider
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val appModules = module {
    single { AppContextProvider() as ContextProvider}
    single {
        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        Retrofit.Builder()
                .baseUrl(BuildConfig.POLL_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
                .build()
                .create(TrainDataService::class.java)
    }

    single { this.androidApplication().getSharedPreferences("prefs", Context.MODE_PRIVATE)  }
    single { LiveNetworkProvider(get()) as NetworkProvider }
}