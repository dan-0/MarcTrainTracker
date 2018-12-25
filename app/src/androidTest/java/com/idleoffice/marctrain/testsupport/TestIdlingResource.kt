package com.idleoffice.marctrain.testsupport

import androidx.test.espresso.idling.CountingIdlingResource
import com.idleoffice.marctrain.idling.IdlingResource
import org.koin.dsl.module.module

class TestIdlingResource: IdlingResource {

    var idlingResource = CountingIdlingResource("test")

    val idlingModule = module {
        single(override = true) { this@TestIdlingResource as IdlingResource }
    }

    override fun startIdlingAction() {
        idlingResource.increment()
    }

    override fun stopIdlingAction() {
        idlingResource.decrement()
    }
}