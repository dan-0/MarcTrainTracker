package com.idleoffice.marctrain.data.model

import com.squareup.moshi.Json

data class TrainStatus(
        @Json(name = "Number") val number: String,
        @Json(name = "Line") val line: String,
        @Json(name = "Direction") val direction: String,
        @Json(name = "NextStation") val nextStation: String,
        @Json(name = "Departure") val departure: String,
        @Json(name = "Status") val status: String,
        @Json(name = "Delay") val delay: String,
        @Json(name = "LastUpdate") val lastUpdate: String,
        @Json(name = "Message") val message: String
)