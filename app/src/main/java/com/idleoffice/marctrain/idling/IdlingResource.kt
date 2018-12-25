package com.idleoffice.marctrain.idling

import com.idleoffice.marctrain.MainApp
import org.koin.dsl.module.module

interface IdlingResource {
    fun startIdlingAction()
    fun stopIdlingAction()
}

val idlingResourceModule = module {
    single { FalseIdle() as IdlingResource}
}