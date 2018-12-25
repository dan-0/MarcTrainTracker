package com.idleoffice.marctrain.testsupport

import com.idleoffice.marctrain.coroutines.CoroutineContextProvider
import kotlinx.coroutines.test.TestCoroutineContext
import kotlin.coroutines.CoroutineContext

class TestCoroutineContextProvider: CoroutineContextProvider {
    val testContext = TestCoroutineContext()
    override val io: CoroutineContext = testContext
    override val ui: CoroutineContext = testContext
}