package com.idleoffice.marctrain.testsupport

import androidx.appcompat.app.AppCompatActivity
import androidx.test.rule.ActivityTestRule
import org.koin.dsl.module.Module
import org.koin.standalone.StandAloneContext.loadKoinModules

class KoinActivityTestRule<T: AppCompatActivity>(
        activityClass: Class<T>,
        initialTouch: Boolean = false,
        launchActivity: Boolean = true,
        val koinModules: List<Module> = listOf()
) : ActivityTestRule<T>(activityClass, initialTouch, launchActivity) {

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        runOnUiThread {
            loadKoinModules(koinModules)
        }
    }
}