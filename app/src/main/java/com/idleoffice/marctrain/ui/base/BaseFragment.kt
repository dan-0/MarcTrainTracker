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
import dagger.android.support.AndroidSupportInjection

abstract class BaseFragment<T : ViewDataBinding, out V : BaseViewModel<*>> : Fragment() {

    private var baseActivity: BaseActivity<T,V>? = null
    var viewDataBinding: T? = null
    private var rootView: View? = null

    abstract val viewModel: V?
    abstract val bindingVariable: Int
    abstract val fragTag: String

    @get:LayoutRes
    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        performDependencyInjection()
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        viewModel?.viewInitialize()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        rootView = viewDataBinding!!.root
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding!!.setVariable(bindingVariable, viewModel)
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
        super.onDetach()
    }

    private fun performDependencyInjection() {
        AndroidSupportInjection.inject(this)
    }

    interface Callback {

        fun onFragmentAttached()

        fun onFragmentDetached(tag: String)
    }

    fun isNetworkConnected() : Boolean {
        return baseActivity != null && baseActivity!!.isNetworkConnected()
    }
}