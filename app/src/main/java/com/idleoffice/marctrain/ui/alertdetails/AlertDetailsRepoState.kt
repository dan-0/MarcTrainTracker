package com.idleoffice.marctrain.ui.alertdetails

sealed class AlertDetailsRepoState {

    object Init : AlertDetailsRepoState()

    object Error : AlertDetailsRepoState()

    data class Content(
        val details: AlertDetails
    ) : AlertDetailsRepoState()
}