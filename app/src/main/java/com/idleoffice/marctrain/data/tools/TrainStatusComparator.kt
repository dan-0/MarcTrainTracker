/*
 * Copyright (c) 2018 IdleOffice Inc.
 *
 * TrainStatusComparator.kt is part of MarcTrainTracker.
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

import com.idleoffice.marctrain.data.model.TrainStatus
import timber.log.Timber
import java.text.ParseException
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
                val o1Date: Date
                val o2Date: Date
                try {
                    o1Date = departureFormatter.parse(o1.departure)
                } catch (e: ParseException) {
                    return -1
                }
                try {
                    o2Date = departureFormatter.parse(o2.departure)
                } catch (e: ParseException) {
                    return 1
                }
                o1Date.compareTo(o2Date)
            }
            else -> o1Val.compareTo(o2Val)
        }
    }
}