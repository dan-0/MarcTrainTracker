/*
 * Copyright (c) 2018 IdleOffice Inc.
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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.idleoffice.marctrain.ui.base

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.idleoffice.marctrain.BR
import io.reactivex.exceptions.UndeliverableException
import kotlinx.android.synthetic.main.progress_bar_frame_layout.*
import timber.log.Timber

abstract class BaseFragment<T : ViewDataBinding, out V : BaseViewModel<*>> : Fragment() {

    private var baseActivity: BaseActivity<T,V>? = null
    private var viewDataBinding: T? = null
    private var rootView: View? = null

    abstract val fragViewModel : V

    private val bindingVariable = BR.viewModel
    val fragTag: String = javaClass.name

    @get:LayoutRes
    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        fragViewModel.viewInitialize()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        rootView = viewDataBinding!!.root
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding!!.setVariable(bindingVariable, fragViewModel)
        viewDataBinding!!.executePendingBindings()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is BaseActivity<*, *>) {
            val activity = context as? BaseActivity<T, V>
            this.baseActivity = activity
            activity?.onFragmentAttached()
        }
    }

    override fun onDetach() {
        baseActivity = null
        Timber.d("Detaching fragment")
        try {
            fragViewModel.compositeDisposable.clear()
        } catch (e: UndeliverableException) {
            // This occurs if an action attempts to call on error after the subscriber is destroyed
            // which is OK here because we're forcibly destroying everything.
            Timber.e(e, "Undeliverable exception occurred")
        }

        super.onDetach()
    }

    interface Callback {

        fun onFragmentAttached()

        fun onFragmentDetached(tag: String)
    }

    fun isNetworkConnected() : Boolean {
        return baseActivity != null && baseActivity!!.isNetworkConnected()
    }

    open fun showLoading(msg: String) {
        Timber.d("Showing loading view.")
        loadingTextView?.text = msg
        loadingView?.visibility = View.VISIBLE
    }

    open fun hideLoading() {
        Timber.d("Hiding loading view.")
        loadingTextView?.text = ""
        loadingView?.visibility = View.GONE
    }


}