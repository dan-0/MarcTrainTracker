package com.idleoffice.marctrain.helpers

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants


internal fun vibrateTap(ctx : Context) {
    val vibe = ctx.getSystemService(VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibe.vibrate(VibrationEffect.createOneShot(HapticFeedbackConstants.KEYBOARD_TAP.toLong(), 1))
    } else {
        // Deprecated, but the replacement is only for 26+
        vibe.vibrate(HapticFeedbackConstants.KEYBOARD_TAP.toLong())
    }
}