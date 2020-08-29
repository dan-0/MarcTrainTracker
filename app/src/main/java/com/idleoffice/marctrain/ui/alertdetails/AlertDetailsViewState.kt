package com.idleoffice.marctrain.ui.alertdetails

sealed class AlertDetailsViewState {

    object Init : AlertDetailsViewState()

    object Loading : AlertDetailsViewState()

    object Error : AlertDetailsViewState()

    data class Content(
        val alertDetails: AlertDetails
    ) : AlertDetailsViewState()
}