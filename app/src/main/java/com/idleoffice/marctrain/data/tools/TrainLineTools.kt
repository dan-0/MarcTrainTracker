/*
 * Copyright (c) 2018 IdleOffice Inc.
 *
 * TrainLineTools.kt is part of MarcTrainTracker.
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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.idleoffice.marctrain.data.tools

class TrainLineTools {

    companion object {
        const val PENN_LINE_IDX = 0
        const val CAMDEN_LINE_IDX = 1
        const val BRUNSWICK_LINE_IDX = 2

        const val DIRECTION_FROM_DC = 0
        const val DIRECTION_TO_DC = 1

        val PENN_STATIONS = listOf(
                "Perryville",
                "Aberdeen",
                "Edgewood",
                "Martin State Airport",
                "Baltimore Penn Station",
                "West Baltimore",
                "Halethorpe",
                "BWI Rail Station",
                "Odenton",
                "Bowie State",
                "Seabrook",
                "New Carrollton",
                "Washington Union Station"
        )

        val CAMDEN_STATIONS = listOf(
                "Baltimore Camden Station",
                "Saint Denis",
                "Dorsey",
                "Jessup",
                "Savage",
                "Laurel Race Track",
                "Laurel",
                "Muirkirk",
                "Greenbelt",
                "College Park",
                "Riverdale",
                "Washington Union Station"
        )

        val BRUNSWICK_STATIONS = listOf(
                "Martinsburg",
                "Duffields",
                "Harpers Ferry",
                "Brunswick",
                "Frederick",
                "Monocacy",
                "Point of Rocks",
                "Dickerson",
                "Barnesville",
                "Boyds",
                "Germantown",
                "Metropolitan Grove",
                "Gaithersburg",
                "Washington Grove",
                "Rockville",
                "Garrett Park",
                "Kensington",
                "Silver Spring",
                "Washington Union Station"
        )
    }
}