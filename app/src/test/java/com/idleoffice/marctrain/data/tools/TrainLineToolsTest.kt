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

import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.RobolectricTest
import com.idleoffice.marctrain.data.tools.TrainLineTools.Companion.BRUNSWICK_LINE_IDX
import com.idleoffice.marctrain.data.tools.TrainLineTools.Companion.CAMDEN_LINE_IDX
import com.idleoffice.marctrain.data.tools.TrainLineTools.Companion.DIRECTION_FROM_DC
import com.idleoffice.marctrain.data.tools.TrainLineTools.Companion.DIRECTION_TO_DC
import com.idleoffice.marctrain.data.tools.TrainLineTools.Companion.PENN_LINE_IDX
import com.idleoffice.marctrain.ui.main.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.robolectric.Robolectric

class TrainLineToolsTest: RobolectricTest() {

    @Test
    fun `canary for spinner arrays incorrectly changed`() {
        val ut = Robolectric.setupActivity(MainActivity::class.java)
        val lineArray = ut.resources.getStringArray(R.array.line_array)

        assertEquals("Penn", lineArray[PENN_LINE_IDX])
        assertEquals("Camden", lineArray[CAMDEN_LINE_IDX])
        assertEquals("Brunswick", lineArray[BRUNSWICK_LINE_IDX])

        val ewArray = ut.resources.getStringArray(R.array.ew_dir_array)
        assertEquals("East", ewArray[DIRECTION_FROM_DC])
        assertEquals("West", ewArray[DIRECTION_TO_DC])

        val nsArray = ut.resources.getStringArray(R.array.ns_dir_array)
        assertEquals("North", nsArray[DIRECTION_FROM_DC])
        assertEquals("South", nsArray[DIRECTION_TO_DC])
    }
}