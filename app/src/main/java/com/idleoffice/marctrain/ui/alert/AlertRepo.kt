package com.idleoffice.marctrain.ui.alert

import kotlinx.coroutines.flow.StateFlow

interface AlertRepo {
    /**
     * Source for repo state updates
     */
    val data: StateFlow<AlertRepoState>

    /**
     * Initiate the fetch for alert data. Results are posted to [data]
     */
    suspend fun fetchAlertData()
}

