package com.idleoffice.marctrain.ui.alert

import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.retrofit.ts.TrainDataService

class FakeAlertTrainDataService(
    var expectedAlertActions: MutableList<() -> List<TrainAlert>> = mutableListOf()
) : TrainDataService {

    override suspend fun getTrainStatus(): List<TrainStatus> {
        throw IllegalArgumentException("This shouldn't be called here")
    }

    override suspend fun getTrainAlerts(): List<TrainAlert> {
        return expectedAlertActions.removeAt(0).invoke()
    }
}