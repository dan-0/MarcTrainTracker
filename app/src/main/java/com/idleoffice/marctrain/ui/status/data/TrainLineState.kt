package com.idleoffice.marctrain.ui.status.data

import com.idleoffice.marctrain.data.tools.Direction
import com.idleoffice.marctrain.data.tools.TrainLine

data class TrainLineState(
    val line: TrainLine = TrainLine.PENN,
    val direction: Direction = Direction.FROM_DC
)