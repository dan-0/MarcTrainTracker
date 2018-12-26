/*
 * Copyright (c) 2018 IdleOffice Inc.
 *
 * AlertFragment.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain.ui.alert

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.data.model.TrainAlert
import com.idleoffice.marctrain.databinding.FragmentAlertBinding
import com.idleoffice.marctrain.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_alert.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class AlertFragment : BaseFragment<FragmentAlertBinding, AlertViewModel>(), AlertNavigator {
    override val fragViewModel: AlertViewModel by viewModel()

    private val alertAdapter: AlertAdapter by inject()

    override val layoutId: Int = R.layout.fragment_alert

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragViewModel.navigator = this
        setAlertObserver()
        retainInstance = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerView()
        showLoading(getString(R.string.looking_for_alerts))
    }

    private fun setAlertObserver() {
        val alertObserver = Observer<List<TrainAlert>> @Synchronized {
            showLoading(getString(R.string.no_alerts_reported_looking))
            if (it.isNullOrEmpty()) {
                trainAlertList.adapter?.notifyDataSetChanged()
            } else {
                Timber.d("New alert received")
                with(alertAdapter.alerts) {
                    clear()
                    addAll(it)
                    hideLoading()
                    trainAlertList.adapter?.notifyDataSetChanged()
                }
            }
        }
        fragViewModel.allAlerts.observe(this, alertObserver)
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

    override fun onDestroyView() {
        super.onDestroyView()
        trainAlertList?.adapter = null
    }
}