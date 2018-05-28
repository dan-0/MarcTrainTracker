package com.idleoffice.marctrain.data.comparator

import com.idleoffice.marctrain.data.model.TrainStatus
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

/**
 * Implements comparator with the last value of stations being greatest
 */
class TrainStatusComparator(private val stations: List<String>): Comparator<TrainStatus> {
    private val departureFormatter = SimpleDateFormat("HH:mm a", Locale.getDefault())

    /**
     * @see Comparator.compare
     *
     * Implements compare on [TrainStatus].
     *
     */
    override fun compare(o1: TrainStatus?, o2: TrainStatus?): Int {
        if (o1 == null && o2 == null) {
            return 0
        }
        o1 ?: return -1
        o2 ?: return 1

        // comparing against lines that don't exist
        if (o1.line != o2.line) {
            return -1
        }

        val o1Val = stations.indexOf(o1.nextStation)
        val o2Val = stations.indexOf(o2.nextStation)

        // If it doesn't exist
        if (o1Val == -1 || o2Val == -1) {
            Timber.e("Trains on a line don't match a given nextStation. \n$o1\n$o2\n$stations")
            return o1Val.compareTo(o2Val)
        }

        return when (o1Val) {
            o2Val -> {
                val o1Date = departureFormatter.parse(o1.departure)
                val o2Date = departureFormatter.parse(o2.departure)
                o1Date.compareTo(o2Date)
            }
            else -> o1Val.compareTo(o2Val)
        }
    }
}