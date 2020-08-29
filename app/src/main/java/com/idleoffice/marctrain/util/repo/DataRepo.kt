package com.idleoffice.marctrain.util.repo

import kotlinx.coroutines.flow.StateFlow

interface DataRepo<T> {
    /**
     * Primary data stream for a Repo. Subscribe [data] changes for updates
     */
    val data: StateFlow<T>
}