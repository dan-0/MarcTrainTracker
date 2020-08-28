package com.idleoffice.marctrain.ui.alert

/**
 * A basic alert to display on the primary alert view. Provides a [description] the train [line] name
 * a [dateTime] for when the alert was published, and a [url] for more details
 */
data class BasicAlert(
    val description: String,
    val line: String,
    val dateTime: String,
    val url: String
)