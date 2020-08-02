/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * TrainLine.kt is part of MarcTrainTracker.
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
package com.idleoffice.marctrain.data.tools

import androidx.annotation.StringRes
import com.idleoffice.marctrain.R

enum class TrainLine(
    val position: Int,
    val lineName: String,
    val toDcDirectionFilterName: String,
    @StringRes val toDcDirectionRes: Int,
    val fromDcDirectionFilterName: String,
    @StringRes val fromDcDirectionRes: Int,
    val stations: List<String>
) {
    PENN(
        position = 0,
        lineName = "Penn",
        toDcDirectionFilterName = "South",
        toDcDirectionRes = R.string.south,
        fromDcDirectionFilterName = "North",
        fromDcDirectionRes = R.string.north,
        stations = StationList.PENN_STATIONS
    ),
    CAMDEN(
        position = 1,
        lineName = "Camden",
        toDcDirectionFilterName = "South",
        toDcDirectionRes = R.string.south,
        fromDcDirectionFilterName = "North",
        fromDcDirectionRes = R.string.north,
        stations = StationList.CAMDEN_STATIONS
    ),
    BRUNSWICK(
        position = 2,
        lineName = "Brunswick",
        toDcDirectionFilterName = "East",
        toDcDirectionRes = R.string.east,
        fromDcDirectionFilterName = "West",
        fromDcDirectionRes = R.string.west,
        stations = StationList.BRUNSWICK_STATIONS
    )
}

