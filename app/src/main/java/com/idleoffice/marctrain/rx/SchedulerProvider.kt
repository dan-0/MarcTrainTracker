package com.idleoffice.marctrain.rx

import io.reactivex.Scheduler


interface SchedulerProvider {
    fun ui() : Scheduler
    fun io() : Scheduler
}