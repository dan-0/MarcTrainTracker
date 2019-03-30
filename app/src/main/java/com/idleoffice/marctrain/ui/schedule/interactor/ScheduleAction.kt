/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * ScheduleAction.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain.ui.schedule.interactor

import com.idleoffice.marctrain.data.AppAction

sealed class ScheduleAction(
        action: String,
        feature: String = "schedule"
) : AppAction(action, feature) {
    class LaunchCamden : ScheduleAction("launch_camden")
    class LaunchPenn : ScheduleAction("launch_penn")
    class LaunchBrunswick : ScheduleAction("launch_brunswick")
}