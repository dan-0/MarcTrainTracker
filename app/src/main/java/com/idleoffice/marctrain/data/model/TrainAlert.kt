package com.idleoffice.marctrain.data.model

import com.squareup.moshi.Json

data class TrainAlert(
        @Json(name = "Desc") val description: String,
        @Json(name = "PubDate") val pubDate: String
)