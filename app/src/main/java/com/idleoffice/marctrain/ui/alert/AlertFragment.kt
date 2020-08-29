/*
 * Copyright (c) 2019 IdleOffice Inc.
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
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.idleoffice.marctrain.ui.alert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.databinding.FragmentAlertBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class AlertFragment : Fragment() {

    private val viewModel: AlertViewModel by viewModel()

    private val alertAdapter: AlertAdapter = AlertAdapter(
        ::loadAlertDetails
    )

    private var _binding: FragmentAlertBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAlerts()
    }

    private fun observeAlerts() {

        viewModel.state.observe(viewLifecycleOwner) {

            when (it) {
                AlertViewState.Init -> {
                    showLoading(getString(R.string.looking_for_alerts))
                    initRecyclerView()
                    viewModel.loadAlerts()
                }
                AlertViewState.Error, // TODO Handler alerts, issue #26
                AlertViewState.NoTrainsFound -> {
                    showLoading(getString(R.string.no_alerts_reported_looking))
                    binding.trainAlertList.adapter?.notifyDataSetChanged()
                }
                is AlertViewState.Content -> {
                    Timber.d("New alert received")
                    hideLoading()
                    alertAdapter.submitList(it.alerts)
                }
            }
        }
    }

    private fun initRecyclerView() {
        with(binding.trainAlertList) {
            itemAnimator = DefaultItemAnimator()
            adapter = alertAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.trainAlertList.adapter = null
        _binding = null
    }

    private fun showLoading(msg: String) {
        Timber.d("Showing loading view.")
        binding.loadingLayout.loadingTextViewPartial.text = msg
        binding.loadingLayout.root.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        Timber.d("Hiding loading view.")
        binding.loadingLayout.loadingTextViewPartial.text = ""
        binding.loadingLayout.root.visibility = View.GONE
    }

    private fun loadAlertDetails(targetUrl: String) {
        val direction = AlertFragmentDirections.actionNavigationAlertToAlertDetailsFragment(targetUrl)
        findNavController().navigate(direction)
    }
}