package com.idleoffice.marctrain.ui.alert

import com.idleoffice.marctrain.data.model.TrainAlert

sealed class AlertViewState {

    object Init : AlertViewState()

    object NoTrainsFound : AlertViewState()

    object Error : AlertViewState()

    data class Content(
        val alerts: List<TrainAlert>
    ) : AlertViewState()
}