/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * TestIdlingResource.kt is part of MarcTrainTracker.
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

import androidx.test.espresso.idling.CountingIdlingResource
import com.idleoffice.marctrain.idling.IdlingResource
import org.koin.dsl.module.module

class TestIdlingResource: IdlingResource {

    var idlingResource = CountingIdlingResource("test")

    val idlingModule = module {
        single(override = true) { this@TestIdlingResource as IdlingResource }
    }

    override fun startIdlingAction() {
        idlingResource.increment()
    }

    override fun stopIdlingAction() {
        if (!idlingResource.isIdleNow) {
            idlingResource.decrement()
        }
    }
}