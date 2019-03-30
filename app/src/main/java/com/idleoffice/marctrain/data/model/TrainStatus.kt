/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * TrainStatus.kt is part of MarcTrainTracker.
 *
 * MarcTrainTracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MarcTrainTracker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

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