package com.idleoffice.marctrain.ui.alert

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.databinding.FragmentAlertBinding
import com.idleoffice.marctrain.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_alert.*
import timber.log.Timber
import javax.inject.Inject

class AlertFragment : BaseFragment<FragmentAlertBinding, AlertViewModel>(), AlertNavigator {

    @Inject
    lateinit var alertAdapter: AlertAdapter

    override val layoutId: Int = R.layout.fragment_alert

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        setAlertObserver()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerView()
        showLoading(getString(R.string.looking_for_alerts))
    }

    private fun setAlertObserver() {
        val alertObserver = Observer<List<TrainAlert>> @Synchronized {
            if (it != null) {
                Timber.d("New alert received")
                with(alertAdapter.alerts) {
                    clear()
                    if(it.isEmpty()) {
                        showLoading(getString(R.string.no_alerts_reported_looking))
                        return@with
                    }
                    addAll(it)
                    hideLoading()
                    trainAlertList?.adapter?.notifyDataSetChanged()
                }
            }
        }
        viewModel.allAlerts.observe(this, alertObserver)
    }

    private fun initRecyclerView() {
        trainAlertList ?: return
        val viewManager = LinearLayoutManager(context)
        trainAlertList.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            itemAnimator = DefaultItemAnimator()
            adapter = alertAdapter
            val divider = DividerItemDecoration(context, viewManager.orientation)
            val drawable = ContextCompat.getDrawable(context, R.drawable.status_divider)

            if(drawable != null) {
                divider.setDrawable(drawable)
            }

            addItemDecoration(divider)
        }
    }

}