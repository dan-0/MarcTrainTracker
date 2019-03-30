/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * KoinActivityTestRule.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain.testsupport

import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.intent.rule.IntentsTestRule
import org.koin.dsl.module.Module
import org.koin.standalone.StandAloneContext.loadKoinModules

class KoinActivityTestRule<T: AppCompatActivity>(
        activityClass: Class<T>,
        initialTouch: Boolean = false,
        launchActivity: Boolean = true,
        val koinModules: List<Module> = listOf()
) : IntentsTestRule<T>(activityClass, initialTouch, launchActivity) {

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        runOnUiThread {
            loadKoinModules(koinModules)
        }
    }
}