/*
 * Copyright (c) 2018 IdleOffice Inc.
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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.idleoffice.marctrain.ui.status

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.idleoffice.marctrain.Const.Companion.PREF_LAST_LINE
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.data.tools.Direction
import com.idleoffice.marctrain.data.tools.Line
import com.idleoffice.marctrain.data.tools.TrainLineTools
import com.idleoffice.marctrain.data.tools.TrainStatusComparator
import com.idleoffice.marctrain.databinding.FragmentStatusCoordinatorBinding
import com.idleoffice.marctrain.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_status_coordinator.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class StatusFragment : BaseFragment<FragmentStatusCoordinatorBinding, StatusViewModel>(), StatusNavigator {

    override val fragViewModel: StatusViewModel by viewModel()

    private val statusAdapter: StatusAdapter by inject()
    private val prefs: SharedPreferences by inject()

    override val layoutId: Int = R.layout.fragment_status_coordinator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragViewModel.navigator = this
        retainInstance = true
        setObservers()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initLineSpinner()
        initRecyclerView()
        showLoading(getString(R.string.looking_for_in_service_trains))
        super.onActivityCreated(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        trainStatusList?.adapter = null
    }

    private fun setObservers() {
        setTrainStatusObserver()
        setLineChangeObserver()
        setDirectionChangeObserver()
    }

    private fun setTrainStatusObserver() {
        val trainStatusObserver = Observer<List<TrainStatus>> @Synchronized {
            if (it != null) {
                Timber.d("New train status received: $it")
                updateTrains()
            }
        }
        fragViewModel.allTrainStatusData.observe(this, trainStatusObserver)
    }

    private fun setLineChangeObserver() {
        val lineChangeObserver = Observer<Int> @Synchronized {
            if (it != null) {
                Timber.d("New line selected: $it")
                val lineString = resources.getStringArray(R.array.line_array)[it]
                val line = Line.resolveLine(lineString)

                parseNewLine(line)
            }
        }
        fragViewModel.selectedTrainLine.observe(this, lineChangeObserver)
    }

    private fun setDirectionChangeObserver() {
        val directionChangeObserver = Observer<Int> @Synchronized {
            if (it != null) {
                updateTrains()
            }
        }

        fragViewModel.selectedTrainDirection.observe(this, directionChangeObserver)
    }

    private fun createLineAdapter(): ArrayAdapter<CharSequence> {
        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.line_array, R.layout.spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }

    private fun createDirAdapter(line: Line): ArrayAdapter<CharSequence> {
        val dirArray = when (line) {
            Line.BRUNSWICK -> R.array.we_dir_array
            else -> R.array.ns_dir_array
        }

        val dirAdapter = ArrayAdapter.createFromResource(requireContext(), dirArray, R.layout.spinner_item)
        dirAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return dirAdapter
    }

    /**
     * Set the direction spinner based on the line number, necessary because some go North-South,
     * some go East-West
     */
    private fun setDirSpinnerOptions(line: Line) {
        directionSpinner?.adapter = createDirAdapter(line)
        directionSpinner?.setSelection(fragViewModel.selectedTrainDirection.value ?: 0)
    }

    /**
     * Parse a new line selection
     */
    private fun parseNewLine(line: Line) {
        directionSpinner ?: return
        prefs.edit().putString(PREF_LAST_LINE, line.name).apply()
        setDirSpinnerOptions(line)
    }

    /**
     * Initialize the line spinner
     */
    private fun initLineSpinner() {
        lineSpinner.adapter = createLineAdapter()

        // TODO retained shared prefs will cause and exception!
        val lineString = prefs.getString(PREF_LAST_LINE, Line.PENN.toString())
        val lastLine = Line.valueOf(lineString!!)

        Timber.d("Parsing new last line: $lastLine")
        val lineIndex = resources.getStringArray(R.array.line_array)
                .indexOfFirst { it.equals(lineString, true) }

        lineSpinner?.setSelection(lineIndex)
        parseNewLine(lastLine)
    }

    private fun initRecyclerView() {
        trainStatusList ?: return
        val viewManager = LinearLayoutManager(context)
        trainStatusList.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = statusAdapter

            layoutManager = viewManager
            val divider = DividerItemDecoration(context, viewManager.orientation)

            val drawable = ContextCompat.getDrawable(context, R.drawable.status_divider)
            if(drawable != null) {
                divider.setDrawable(drawable)
            }

            addItemDecoration(divider)
        }
    }

    override fun showLoading(msg: String) {
        lineSpinner?.isClickable = false
        directionSpinner?.isClickable = false
        super.showLoading(msg)
    }

    override fun hideLoading() {
        lineSpinner?.isClickable = true
        directionSpinner?.isClickable = true
        super.hideLoading()
    }

    private fun updateTrains() {
        val allTrains = fragViewModel.allTrainStatusData.value

        if (allTrains.isNullOrEmpty()) {
            statusAdapter.trainStatuses.clear()
            statusAdapter.notifyDataSetChanged()
            showLoading(getString(R.string.no_active_trains))
            return
        }

        val selectedLine =
                resolveLineFromPosition(fragViewModel.selectedTrainLine.value ?: 0)
        val selectedDirection =
                Direction.resolveDirectionFromPosition(fragViewModel.selectedTrainDirection.value ?: 0)

        val lineString = when (selectedLine) {
            Line.PENN -> getString(R.string.penn)
            Line.CAMDEN -> getString(R.string.camden)
            else -> getString(R.string.brunswick)
        }

        val directionString = resolveDirectionString(selectedLine, selectedDirection)

        var compareArray = when(selectedLine) {
            Line.PENN -> TrainLineTools.PENN_STATIONS
            Line.CAMDEN -> TrainLineTools.CAMDEN_STATIONS
            else -> TrainLineTools.BRUNSWICK_STATIONS
        }

        if (selectedDirection == Direction.TO_DC) {
            compareArray = compareArray.asReversed()
        }

        val currentTrains = allTrains.filter {
            (it.direction == directionString && it.line == lineString)
        }.sortedWith(TrainStatusComparator(compareArray))

        with(statusAdapter.trainStatuses) {
            clear()
            if(currentTrains.isEmpty()) {
                trainStatusList?.adapter?.notifyDataSetChanged()
                showLoading(getString(R.string.no_active_trains))
                return@with
            }
            addAll(currentTrains)
            hideLoading()
            trainStatusList?.adapter?.notifyDataSetChanged()
        }

        statusCollapsing?.title = "$lineString $directionString"
    }

    private fun resolveLineFromPosition(position: Int): Line {
        val lineString = resources.getStringArray(R.array.line_array)[position]
        return Line.resolveLine(lineString)
    }

    private fun resolveDirectionString(line: Line, selectedDirection: Direction): String {
        return when(line) {
            Line.BRUNSWICK -> {
                when (selectedDirection) {
                    Direction.FROM_DC -> resources.getString(R.string.west)
                    else -> resources.getString(R.string.east)
                }
            }
            else -> {
                when (selectedDirection) {
                    Direction.FROM_DC -> resources.getString(R.string.north)
                    else -> resources.getString(R.string.south)
                }
            }
        }
    }
}