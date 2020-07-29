/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * _Okhttp.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain.okhttp

import okhttp3.Interceptor
import okhttp3.ResponseBody

fun Interceptor.Chain.getContent(body: ResponseBody?): ByteArray {
    return if (request().url().encodedPath().contains("trainData")) {
        "[{\"Number\":\"857\",\"Line\":\"Camden\",\"Direction\":\"South\",\"NextStation\":\"Washington Union Station\",\"Departure\":\"06:28 PM\",\"Status\":\"On Time\",\"Delay\":\"\",\"LastUpdate\":\"\",\"Message\":\"\"},{\"Number\":\"P875\",\"Line\":\"Brunswick\",\"Direction\":\"West\",\"NextStation\":\"Martinsburg\",\"Departure\":\"06:45 PM\",\"Status\":\"On Time\",\"Delay\":\"\",\"LastUpdate\":\"\",\"Message\":\"\"},{\"Number\":\"P893\",\"Line\":\"Brunswick\",\"Direction\":\"West\",\"NextStation\":\"Monocacy\",\"Departure\":\"06:49 PM\",\"Status\":\"On Time\",\"Delay\":\"\",\"LastUpdate\":\"\",\"Message\":\"\"},{\"Number\":\"P881\",\"Line\":\"Brunswick\",\"Direction\":\"West\",\"NextStation\":\"Silver Spring\",\"Departure\":\"06:34 PM\",\"Status\":\"On Time\",\"Delay\":\"\",\"LastUpdate\":\"\",\"Message\":\"\"},{\"Number\":\"414\",\"Line\":\"Penn\",\"Direction\":\"North\",\"NextStation\":\"-- Pending --\",\"Departure\":\"\",\"Status\":\"Canceled\",\"Delay\":\"\",\"LastUpdate\":\"6:58 AM 4/14/20\",\"Message\":\"\"},{\"Number\":\"536\",\"Line\":\"Penn\",\"Direction\":\"North\",\"NextStation\":\"Aberdeen\",\"Departure\":\"06:31 PM\",\"Status\":\"On Time\",\"Delay\":\"\",\"LastUpdate\":\"\",\"Message\":\"\"},{\"Number\":\"544\",\"Line\":\"Penn\",\"Direction\":\"North\",\"NextStation\":\"New Carrollton\",\"Departure\":\"06:35 PM\",\"Status\":\"On Time\",\"Delay\":\"\",\"LastUpdate\":\"\",\"Message\":\"\"},{\"Number\":\"449\",\"Line\":\"Penn\",\"Direction\":\"South\",\"NextStation\":\"Baltimore Penn Station\",\"Departure\":\"06:35 PM\",\"Status\":\"On Time\",\"Delay\":\"\",\"LastUpdate\":\"\",\"Message\":\"\"}]".toByteArray()
    } else {
        body?.bytes() ?: ByteArray(0)
    }
}