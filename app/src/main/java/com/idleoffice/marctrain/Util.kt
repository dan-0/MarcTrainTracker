/*
 * Copyright (c) 2018 IdleOffice Inc.
 *
 * Util.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants

internal fun Context.vibrateTap() {
    val vibe = this.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    vibe ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibe.vibrate(VibrationEffect.createOneShot(HapticFeedbackConstants.KEYBOARD_TAP.toLong(), 1))
    } else {
        // Deprecated, but the replacement is only for 26+, so must use deprecated version
        @Suppress("DEPRECATION")
        vibe.vibrate(HapticFeedbackConstants.KEYBOARD_TAP.toLong())
    }
}

class Const {
    companion object {
        const val PREF_LAST_LINE = "lastLinev2"
    }
}