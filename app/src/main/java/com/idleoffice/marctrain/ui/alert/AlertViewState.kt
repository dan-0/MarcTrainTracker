package com.idleoffice.marctrain.ui.alert

sealed class AlertViewState {

    object Init : AlertViewState()

    object NoTrainsFound : AlertViewState()

    object Error : AlertViewState()

    data class Content(
        val alerts: List<BasicAlert>
    ) : AlertViewState()
}