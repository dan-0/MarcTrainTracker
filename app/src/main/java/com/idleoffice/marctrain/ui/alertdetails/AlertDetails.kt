package com.idleoffice.marctrain.ui.alertdetails

/**
 * View layer alert details data
 */
data class AlertDetails(
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val affectedRoutes: String,
    val cause: String,
    val effect: String
)