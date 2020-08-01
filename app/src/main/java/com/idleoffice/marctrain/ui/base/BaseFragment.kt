/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * BaseFragment.kt is part of MarcTrainTracker.
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
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.idleoffice.marctrain.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.idleoffice.marctrain.analytics.FirebaseService
import org.koin.android.ext.android.inject

abstract class BaseFragment<out V : BaseViewModel> : Fragment() {

    protected val analyticService: FirebaseService by inject()

    abstract val fragViewModel : V

    val fragTag: String = javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(fragViewModel)
        setHasOptionsMenu(false)
    }
}