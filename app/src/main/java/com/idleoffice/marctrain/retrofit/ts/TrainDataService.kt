/*
 * Copyright (c) 2018 IdleOffice Inc.
 *
 * TrainDataService.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain.retrofit.ts

import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.data.model.TrainStatus
import io.reactivex.Observable
import retrofit2.http.GET

interface TrainDataService {
    @GET("trainData")
    fun getTrainStatus() : Observable<List<TrainStatus>>

    @GET("alertData")
    fun getTrainAlerts() : Observable<List<TrainAlert>>
}