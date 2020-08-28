package com.idleoffice.marctrain.ui.alert

/**
 * State of the Alert Repo
 */
sealed class AlertRepoState {

    object Init : AlertRepoState()

    object Error : AlertRepoState()

    data class Content(
        val alerts: List<BasicAlert>
    ) : AlertRepoState()
}