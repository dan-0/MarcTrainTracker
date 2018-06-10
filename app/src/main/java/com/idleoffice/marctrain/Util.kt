package com.idleoffice.marctrain

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import com.idleoffice.marctrain.rx.SchedulerProvider
import io.reactivex.Observable

fun <T: Any> Observable<T>.observeSubscribe(schedulerProvider: SchedulerProvider): Observable<T> {
    return this.observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io())
}

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
        const val PREF_LAST_LINE = "lastLine"
    }
}