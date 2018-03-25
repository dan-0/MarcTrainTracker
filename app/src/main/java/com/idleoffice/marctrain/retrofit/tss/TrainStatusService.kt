package com.idleoffice.marctrain.retrofit.tss

import com.idleoffice.marctrain.data.model.TrainStatus
import io.reactivex.Observable
import retrofit2.http.GET

interface TrainStatusService {
    @GET("trainData")
    fun getTrainData() : Observable<List<TrainStatus>>
}