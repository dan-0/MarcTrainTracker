package com.idleoffice.marctrain

import com.idleoffice.marctrain.rx.SchedulerProvider
import io.reactivex.Observable

fun <T: Any> Observable<T>.observeSubscribe(schedulerProvider: SchedulerProvider): Observable<T> {
    return this.observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io())
}

class Const {
    companion object {
        const val PREF_LAST_LINE = "lastLine"
    }
}