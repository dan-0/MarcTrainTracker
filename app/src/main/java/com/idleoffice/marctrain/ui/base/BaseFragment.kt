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
        fragViewModel.compositeDisposable.clear()
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