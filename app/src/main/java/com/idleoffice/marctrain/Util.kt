package com.idleoffice.marctrain

import com.idleoffice.marctrain.rx.SchedulerProvider
import io.reactivex.Observable

fun <T: Any> Observable<T>.observeSubscribe(schedulerProvider: SchedulerProvider): Observable<T> {
    return this.observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io())
}

class Const {
    companion object {
        const val PREF_LAST_LINE = "lastLine"
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