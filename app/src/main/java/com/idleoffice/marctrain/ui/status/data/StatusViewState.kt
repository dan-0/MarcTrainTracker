package com.idleoffice.marctrain.ui.status.data

import com.idleoffice.marctrain.data.model.TrainStatus

sealed class StatusViewState {
    open val trainLineState: TrainLineState = TrainLineState()

    data class Init(
        override val trainLineState: TrainLineState = TrainLineState()
    ) : StatusViewState()

    data class Content(
        val allTrains: List<TrainStatus>,
        val filteredTrains: List<TrainStatus>,
        override val trainLineState: TrainLineState
    ) : StatusViewState()
}