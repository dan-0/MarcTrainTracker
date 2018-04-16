package com.idleoffice.marctrain.helpers

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Vibrator
import android.view.HapticFeedbackConstants


internal fun vibrateTap(ctx : Context) {
    val vibe = ctx.getSystemService(VIBRATOR_SERVICE) as Vibrator
    vibe.vibrate(HapticFeedbackConstants.KEYBOARD_TAP.toLong())
}