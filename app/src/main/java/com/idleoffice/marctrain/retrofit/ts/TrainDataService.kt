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