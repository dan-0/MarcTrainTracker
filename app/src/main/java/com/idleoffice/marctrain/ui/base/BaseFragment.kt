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

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.idleoffice.marctrain.analytics.FirebaseService
import org.koin.android.ext.android.inject
import timber.log.Timber

abstract class BaseFragment<T : ViewDataBinding, out V : BaseViewModel> : Fragment() {

    private var baseActivity: BaseActivity<T,V>? = null
    protected lateinit var rootView: View
    protected val analyticService: FirebaseService by inject()

    abstract val fragViewModel : V

    val fragTag: String = javaClass.name

    @get:LayoutRes
    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycle.addObserver(fragViewModel)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is BaseActivity<*, *>) {
            val activity = context as? BaseActivity<T, V>
            this.baseActivity = activity
        }
    }

    override fun onDetach() {
        baseActivity = null
        Timber.d("Detaching fragment")
        super.onDetach()
    }
}