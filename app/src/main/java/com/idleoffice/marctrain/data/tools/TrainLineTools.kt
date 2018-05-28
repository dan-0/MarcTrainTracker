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