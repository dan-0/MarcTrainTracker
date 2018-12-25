/*
 * Copyright (c) 2018 IdleOffice Inc.
 *
 * TrainLineToolsTest.kt is part of MarcTrainTracker.
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

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.idleoffice.marctrain.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrainLineToolsTest {

    @Test
    fun canaryEnsureSpinnerPositions() {
        val ut = InstrumentationRegistry.getInstrumentation().targetContext
        val ewArray = ut.resources.getStringArray(R.array.we_dir_array)
        assertEquals("East", ewArray[Direction.TO_DC.position])
        assertEquals("West", ewArray[Direction.FROM_DC.position])

        val nsArray = ut.resources.getStringArray(R.array.ns_dir_array)
        assertEquals("South", nsArray[Direction.TO_DC.position])
        assertEquals("North", nsArray[Direction.FROM_DC.position])
    }
}