/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * FirebaseServiceImpl.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.idleoffice.marctrain.BuildConfig
import com.idleoffice.marctrain.data.AppAction
import com.idleoffice.marctrain.data.AppEvent

class FirebaseServiceImpl(context: Context) : FirebaseService {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    private val environment = "${BuildConfig.FLAVOR}${BuildConfig.BUILD_TYPE}"

    override fun newEvent(event: AppEvent) {
        Bundle().run {
            putString("feature", event.feature)
            putString("event", event.event)
            putString("environment", environment)
            firebaseAnalytics.logEvent("event", this)
        }
    }

    override fun newAction(action: AppAction) {
        Bundle().run {
            putString("feature", action.feature)
            putString("action", action.action)
            putString("environment", environment)
            firebaseAnalytics.logEvent("action", this)
        }
    }
}