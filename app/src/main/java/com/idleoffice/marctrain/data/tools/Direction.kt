package com.idleoffice.marctrain.data.tools

import androidx.annotation.StringRes
import com.idleoffice.marctrain.R

enum class Direction(
    val position: Int,
    @StringRes val northSouthStringRes: Int,
    @StringRes val eastWestStringRes: Int
) {
    FROM_DC(0, R.string.north, R.string.south),
    TO_DC(1, R.string.east, R.string.west);

    companion object {

        /**
         * Resolves a direction given a [position]
         */
        fun resolveDirectionFromPosition(position: Int): Direction {
            return values().first { it.position == position }
        }
    }
}