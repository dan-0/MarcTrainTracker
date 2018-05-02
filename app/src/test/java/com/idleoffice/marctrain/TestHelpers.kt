package com.idleoffice.marctrain

import com.idleoffice.marctrain.rx.SchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.mockito.Mockito

class TrampolineSchedulerProvider : SchedulerProvider {
    override fun ui(): Scheduler {return Schedulers.trampoline()}
    override fun io(): Scheduler {return Schedulers.trampoline()}
}

class TestSchedulerProvider(private val ts: TestScheduler) : SchedulerProvider {
    override fun ui(): Scheduler { return ts }
    override fun io(): Scheduler { return ts }
}

fun <T> any(): T {
    Mockito.any<T>()
    return null as T
}