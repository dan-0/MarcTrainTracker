/*
 * Copyright (c) 2019 IdleOffice Inc.
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
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.idleoffice.marctrain.data.tools

enum class Line {
    PENN,
    CAMDEN,
    BRUNSWICK;

    companion object {

        /**
         * Resolves a line given a matching [stringLine]
         */
        fun resolveLine(stringLine: String): Line {
            return values().first { stringLine.equals(it.name, true) }
        }
    }
}

enum class Direction(val position: Int) {
    FROM_DC(0),
    TO_DC(1);

    companion object {

        /**
         * Resolves a direction given a [position]
         */
        fun resolveDirectionFromPosition(position: Int): Direction {
            return values().first { it.position == position }
        }
    }
}

class TrainLineTools {

    companion object {

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