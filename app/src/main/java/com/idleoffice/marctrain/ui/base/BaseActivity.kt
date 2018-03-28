package com.idleoffice.marctrain.ui.base

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import android.widget.ProgressBar
import dagger.android.AndroidInjection
import io.fabric.sdk.android.services.network.NetworkUtils
import timber.log.Timber


abstract class BaseActivity <T : ViewDataBinding, V : BaseViewModel<*>> : AppCompatActivity(), BaseFragment.Callback {

    private var progressBar: ProgressBar? = null
    var viewDataBinding : T? = null
    var viewModel : V? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        performDependencyInjection()
        Timber.d("Base activity onCreate called")
        super.onCreate(savedInstanceState)
        if(viewModel == null) {
            viewModel = getActivityViewModel()
        }
        initDataBinding()
        viewModel?.viewInitialize()
    }

    private fun initDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, layoutId)
        viewDataBinding?.setVariable(bindingVariable, viewModel)
        viewDataBinding?.executePendingBindings()
    }

    fun showLoading() {
        if(progressBar == null) {
            progressBar = getProgressBar() ?: return
        }
        Timber.d("Showing progress bar.")
        window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
        progressBar?.visibility = View.VISIBLE
    }

    fun hideLoading() {
        progressBar?.visibility = View.GONE
        Timber.d("Hiding progress bar.")
        window.clearFlags(FLAG_NOT_TOUCHABLE)
    }

    /**
     * @return
     *      The view model
     */
    abstract fun getActivityViewModel() : V

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

    private fun performDependencyInjection() {
        AndroidInjection.inject(this)
    }

    fun isNetworkConnected() : Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val network = cm?.activeNetworkInfo
        return network != null && network.isConnectedOrConnecting
    }
}