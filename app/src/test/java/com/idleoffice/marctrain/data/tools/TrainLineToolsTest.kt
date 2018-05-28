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