package com.idleoffice.marctrain.ui.alertdetails

import com.idleoffice.marctrain.util.repo.DataRepo

interface AlertDetailsRepo : DataRepo<AlertDetailsRepoState> {

    /**
     * Load the alert details from the given [url]
     */
    suspend fun loadDetails(url: String)
}