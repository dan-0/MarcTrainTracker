/*
 * Copyright (c) 2018 IdleOffice Inc.
 *
 * BaseViewModel.kt is part of MarcTrainTracker.
 *
 * MarcTrainTracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MarcTrainTracker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.idleoffice.marctrain.ui.base

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.idleoffice.marctrain.coroutines.ContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import timber.log.Timber


abstract class BaseViewModel<T>(val contextProvider: ContextProvider)
    : ViewModel(), LifecycleObserver {

    var navigator : T? = null

    val isLoading = ObservableBoolean(false)

    private var initialized = false

    /**
     * Job scoped to an active instance of this [ViewModel]. It is canceled in `onCleared` and
     * reinitialized if it is canceled when the [ViewModel] reinitializes
     */
    private var job: Job = Job()

    protected var ioScope = CoroutineScope(contextProvider.io + job)
    protected var mainScope = CoroutineScope(contextProvider.ui + job)

    /**
     * A one time initialization function to help with testing. `init{}` isn't as controllable
     * in unit tests. This requires a view to exist in order to initialize the data
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun viewInitialize() {
        // job value is retained after onDestroy(), but in a canceled state so it needs to be reset
        if (job.isCancelled) {
            job = Job()
        }

        if (!initialized) {
            initialized = true
            initialize()
        }
    }

    protected open fun initialize() {}

    override fun onCleared() {
        Timber.d("Clearing jobs")
        job.cancel()

        super.onCleared()
    }
}