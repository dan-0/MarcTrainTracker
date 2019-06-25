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
        "[{\"Number\":\"694\",\"Line\":\"Penn\",\"Direction\":\"North\",\"NextStation\":\"Martin State Airport\",\"Departure\":\"09:23 PM\",\"Status\":\"Delayed\",\"Delay\":\"20 Min\",\"LastUpdate\":\"8:26 PM 6/23/19\",\"Message\":\"\"}]".toByteArray()
    } else {
        body?.bytes() ?: ByteArray(0)
    }
}