package com.idleoffice.marctrain.ui.alert

data class AlertData(
    val title: String,
    val startDate: String?,
    val endDate: String?,
    val description: String?,
    val affectedRoutes: List<String>,
    val cause: String?
)