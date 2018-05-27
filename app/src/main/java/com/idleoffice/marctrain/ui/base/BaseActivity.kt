package com.idleoffice.marctrain.ui.base

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import timber.log.Timber

abstract class BaseActivity <T : ViewDataBinding, out V : BaseViewModel<*>> : AppCompatActivity(), BaseFragment.Callback {

    private var progressBar: ProgressBar? = null
    var viewDataBinding : T? = null
    abstract val actViewModel : V


    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("Base activity onCreate called")
        super.onCreate(savedInstanceState)
        initDataBinding()
        actViewModel.viewInitialize()
    }

    private fun initDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, layoutId)
        viewDataBinding?.setVariable(bindingVariable, actViewModel)
        viewDataBinding?.executePendingBindings()
    }

    fun showLoading() {
        if(progressBar == null) {
            progressBar = getProgressBar() ?: return
        }
        Timber.d("Showing progress bar.")
        progressBar?.visibility = View.VISIBLE
    }

    fun hideLoading() {
        progressBar?.visibility = View.GONE
        Timber.d("Hiding progress bar.")
    }

    /**
     *  The variable ID
     */
    abstract val bindingVariable : Int

    open fun getProgressBar() : ProgressBar? {
        return null
    }

    /**
     *  The Layout ID
     */
    @get:LayoutRes
    abstract val layoutId : Int

    fun isNetworkConnected() : Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val network = cm?.activeNetworkInfo
        return network != null && network.isConnectedOrConnecting
    }

    override fun onFragmentAttached() {}
    override fun onFragmentDetached(tag: String) {}
}