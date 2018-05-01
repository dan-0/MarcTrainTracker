package com.idleoffice.marctrain

import com.idleoffice.marctrain.rx.SchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class TrampolineSchedulerProvider : SchedulerProvider {
    override fun ui(): Scheduler {return Schedulers.trampoline()}
    override fun io(): Scheduler {return Schedulers.trampoline()}
}
