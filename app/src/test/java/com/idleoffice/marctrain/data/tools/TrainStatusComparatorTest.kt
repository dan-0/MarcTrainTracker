/*
 * Copyright (c) 2018 IdleOffice Inc.
 *
 * TrainStatusComparatorTest.kt is part of MarcTrainTracker.
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

import com.idleoffice.marctrain.DummyTrainStatusBuilder
import com.idleoffice.marctrain.data.tools.TrainLineTools.Companion.PENN_STATIONS
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TrainStatusComparatorTest {

    private val ut = TrainStatusComparator(PENN_STATIONS)

    @Test
    fun `compare two equal stations with different times`() {
        val o1 = DummyTrainStatusBuilder().line("Penn").direction("North")
                .nextStation("Seabrook").departure("2:35 PM").build()
        val o2 = DummyTrainStatusBuilder().line("Penn").direction("South")
                .nextStation("Seabrook").departure("2:34 PM").build()

        assertEquals(1, ut.compare(o1, o2))
        // flip and ensure
        assertEquals(-1, ut.compare(o2, o1))
    }

    @Test
    fun `test equal stations`() {
        val o1 = DummyTrainStatusBuilder().line("Penn").direction("North")
                .nextStation("Seabrook").departure("2:34 PM").build()
        val o2 = DummyTrainStatusBuilder().line("Penn").direction("South")
                .nextStation("Seabrook").departure("2:34 PM").build()

        assertEquals(0, ut.compare(o1, o2))
        // flip
        assertEquals(0, ut.compare(o2, o1))
    }

    @Test
    fun `null stations`() {
        val notNull = DummyTrainStatusBuilder().line("Penn").direction("North")
                .nextStation("Seabrook").departure("2:34 PM").build()

        assertEquals(-1, ut.compare(null, notNull))
        assertEquals(1, ut.compare(notNull, null))
        assertEquals(0, ut.compare(null, null))
    }

    @Test
    fun `station not found`() {
        val validStation = DummyTrainStatusBuilder().line("Penn").direction("North")
                .nextStation("Seabrook").departure("2:34 PM").build()
        val badStation = DummyTrainStatusBuilder().line("Penn").direction("North")
                .nextStation("asefa4323").departure("2:34 PM").build()

        assertEquals(1, ut.compare(validStation, badStation))
        assertEquals(-1, ut.compare(badStation, validStation))
        assertEquals(0, ut.compare(badStation, badStation))
        assertEquals(0, ut.compare(validStation, validStation))
    }

}