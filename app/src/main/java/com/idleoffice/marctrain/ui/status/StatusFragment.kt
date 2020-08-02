/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * StatusFragment.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain.ui.status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.data.tools.Direction
import com.idleoffice.marctrain.data.tools.TrainLine
import com.idleoffice.marctrain.databinding.FragmentStatusCoordinatorBinding
import com.idleoffice.marctrain.ui.status.data.StatusViewState
import com.idleoffice.marctrain.ui.status.data.TrainLineState
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class StatusFragment : Fragment() {

    private val viewModel: StatusViewModel by viewModel()

    private val statusAdapter: StatusAdapter = StatusAdapter()

    private var _binding: FragmentStatusCoordinatorBinding? = null
    private val binding get() = _binding!!

    private val lineAdapter: ArrayAdapter<String> by lazy { createLineAdapter() }

    private val lineListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            viewModel.updateLine(position)
        }
    }

    private val directionListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            viewModel.updateDirection(position)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentStatusCoordinatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSpinnerStates()
        setObservers()

        initRecyclerView()
        showLoading(getString(R.string.looking_for_in_service_trains))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.trainStatusList.adapter = null
    }

    private fun setObservers() {
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is StatusViewState.Init -> handleInit()
                is StatusViewState.Content -> handleContent(it.filteredTrains, it.trainLineState)
            }
        }
    }

    private fun handleInit() {
        viewModel.loadTrainStatus()
    }

    private fun handleContent(trains: List<TrainStatus>, state: TrainLineState) {
        updateTrains(trains, state)

        val directionPosition = state.direction.position

        with(binding.directionSpinner) {
            adapter = createDirAdapter(state.line)

            if (selectedItemPosition != directionPosition) {
                binding.directionSpinner.setSelection(directionPosition)
            }
        }

        with(binding.lineSpinner) {
            val linePosition = state.line.position
            if (selectedItemPosition != linePosition) {
                setSelection(linePosition)
            }
        }
    }

    private fun initSpinnerStates(currentLine: TrainLine = TrainLine.PENN) {
        with (binding.lineSpinner) {
            adapter = lineAdapter
            onItemSelectedListener = lineListener
        }

        with(binding.directionSpinner) {
            adapter = createDirAdapter(currentLine)
            onItemSelectedListener = directionListener
        }
    }

    private fun createLineAdapter(): ArrayAdapter<String> {
        return TrainLine.values().map {
            it.lineName
        }.let {
            ArrayAdapter(requireContext(), R.layout.spinner_item, it)
        }.apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun createDirAdapter(line: TrainLine): ArrayAdapter<String> {
        return listOf(
            getString(line.fromDcDirectionRes),
            getString(line.toDcDirectionRes)
        ).let {
            ArrayAdapter(requireContext(), R.layout.spinner_item, it)
        }.apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun initRecyclerView() {
        val viewManager = LinearLayoutManager(context)

        binding.trainStatusList.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = statusAdapter

            layoutManager = viewManager
            val divider = DividerItemDecoration(context, viewManager.orientation)

            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.status_divider)
            if(drawable != null) {
                divider.setDrawable(drawable)
            }

            addItemDecoration(divider)
        }
    }

    private fun showLoading(msg: String) {
        Timber.d("Showing loading view.")
        binding.lineSpinner.isClickable = false
        binding.directionSpinner.isClickable = false
        binding.loadingLayout.loadingTextViewPartial.text = msg
        binding.loadingLayout.root.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.lineSpinner.isClickable = true
        binding.directionSpinner.isClickable = true
        binding.loadingLayout.loadingTextViewPartial.text = ""
        binding.loadingLayout.root.visibility = View.GONE
    }

    private fun updateTrains(trains: List<TrainStatus>, lineState: TrainLineState) {

        statusAdapter.submitList(trains)

        val lineString = lineState.line.lineName
        val directionString = when (lineState.direction) {
            Direction.FROM_DC -> lineState.line.fromDcDirectionRes
            Direction.TO_DC -> lineState.line.toDcDirectionRes
        }.let {
            getString(it)
        }

        binding.statusCollapsing.title = "$lineString $directionString"

        if (trains.isEmpty()) {
            showLoading(getString(R.string.no_active_trains))
        } else {
            hideLoading()
        }
    }
}