/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * AppEvent.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain.data

import com.idleoffice.marctrain.BuildConfig

/**
 * Base Event emitted from a non-user Event
 *
 * @param event Event that occurred
 * @param feature Feature the [AppEvent] is related to
 */
abstract class AppEvent(
        val event: String,
        val feature: String
)