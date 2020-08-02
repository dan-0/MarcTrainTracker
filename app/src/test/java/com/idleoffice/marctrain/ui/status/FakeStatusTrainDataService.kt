package com.idleoffice.marctrain.ui.status

import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.retrofit.ts.TrainDataService

class FakeStatusTrainDataService(
    var expectedStatusActions: MutableList<() -> List<TrainStatus>> = mutableListOf()
) : TrainDataService {

    var counter = 0

    override suspend fun getTrainStatus(): List<TrainStatus> {
        return expectedStatusActions.removeAt(0).invoke()
    }

    override suspend fun getTrainAlerts(): List<TrainAlert> {
        throw IllegalArgumentException("This shouldn't be called here")
    }
}