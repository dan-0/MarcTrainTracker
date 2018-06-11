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

import android.arch.lifecycle.Observer
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.ArrayAdapter
import com.idleoffice.marctrain.Const.Companion.PREF_LAST_LINE
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.data.tools.TrainLineTools.Companion.DIRECTION_FROM_DC
import com.idleoffice.marctrain.databinding.FragmentStatusCoordinatorBinding
import com.idleoffice.marctrain.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_status_coordinator.*
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject
import timber.log.Timber

class StatusFragment : BaseFragment<FragmentStatusCoordinatorBinding, StatusViewModel>(), StatusNavigator {
    override val fragViewModel: StatusViewModel by viewModel()

    private val statusAdapter: StatusAdapter by inject()
    private val prefs: SharedPreferences by inject()

    override val layoutId: Int = R.layout.fragment_status_coordinator
    private val spinnerItem = R.layout.spinner_item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragViewModel.navigator = this
        setTrainStatusObserver()
        setLineChangeObserver()
        setTitleChangedObserver()
        retainInstance = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initLineSpinner()
        initRecyclerView()
        showLoading(getString(R.string.looking_for_in_service_trains))
        super.onActivityCreated(savedInstanceState)
    }

    private fun setTrainStatusObserver() {
        val trainStatusObserver = Observer<List<TrainStatus>> @Synchronized {
            if (it != null) {
                Timber.d("New train status received")
                with(statusAdapter.trainStatuses) {
                    clear()
                    if(it.isEmpty()) {
                        showLoading(getString(R.string.no_active_trains))
                        trainStatusList?.adapter?.notifyDataSetChanged()
                        return@with
                    }
                    addAll(it)
                    hideLoading()
                    trainStatusList.adapter?.notifyDataSetChanged()
                }
            }
        }
        fragViewModel.currentTrainStatusData.observe(this, trainStatusObserver)
    }

    private fun setLineChangeObserver() {
        val lineChangeObserver = Observer<Int> @Synchronized {
            if (it != null) {
                Timber.d("New line selected: $it")
                parseNewLine(it)
            }
        }

        fragViewModel.selectedTrainLine.observe(this, lineChangeObserver)
    }

    private fun setTitleChangedObserver() {
        val titleChangedObserver = Observer<String> @Synchronized {
            if (it != null) {
                Timber.d("Direction changed $it")
                statusCollapsing?.title = it
            }
        }

        fragViewModel.title.observe(this, titleChangedObserver)
    }

    /**
     * Set the direction spinner based on the line number, necessary because some go North-South,
     * some go East-West
     */
    private fun setDirSpinner(lineNum: Int) {

        // Determine if we need to use North/South or East/West directions
        val array = resources.getStringArray(R.array.line_array)
        var dirArray = R.array.ns_dir_array
        if(array[lineNum] == array[2]) {
            dirArray = R.array.ew_dir_array
        }

        val dirAdapter = ArrayAdapter.createFromResource(context, dirArray, spinnerItem)
        dirAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        directionSpinner?.adapter = dirAdapter
    }

    /**
     * Parse a new line selection
     */
    private fun parseNewLine(lineNum: Int) {
        directionSpinner ?: return

        val array = resources.getStringArray(R.array.line_array)

        if( lineNum < 0 || lineNum >= array.size) {
            Timber.d("Invalid array item: $lineNum of ${array.size - 1}")
            return
        }

        prefs.edit().putInt(PREF_LAST_LINE, lineNum).apply()

        setDirSpinner(lineNum)
    }

    /**
     * Initialize the line spinner
     */
    private fun initLineSpinner() {
        val lineAdapter = ArrayAdapter.createFromResource(context, R.array.line_array, spinnerItem)
        lineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        lineSpinner.adapter = lineAdapter

        val lastLine = prefs.getInt(PREF_LAST_LINE, DIRECTION_FROM_DC)
        Timber.d("Parsing new last line: $lastLine")
        lineSpinner?.setSelection(lastLine)
        setDirSpinner(lastLine)
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
}