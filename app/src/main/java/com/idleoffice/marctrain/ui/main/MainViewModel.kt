package com.idleoffice.marctrain.ui.main

import android.app.Application
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.idleoffice.marctrain.ui.base.BaseViewModel


class MainViewModel(
        app : Application,
        schedulerProvider: SchedulerProvider)
    : BaseViewModel<MainNavigator>(app, schedulerProvider)