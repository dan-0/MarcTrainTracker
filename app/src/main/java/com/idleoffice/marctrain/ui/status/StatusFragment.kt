package com.idleoffice.marctrain.ui.status

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.WindowManager
import android.widget.ArrayAdapter
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.databinding.FragmentStatusCoordinatorBinding
import com.idleoffice.marctrain.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_status_coordinator.*
import timber.log.Timber
import javax.inject.Inject

class StatusFragment : BaseFragment<FragmentStatusCoordinatorBinding, StatusViewModel>(), StatusNavigator {

    @Inject
    lateinit var statusAdapter: StatusAdapter

    override val layoutId: Int = R.layout.fragment_status_coordinator
    private val spinnerItem = R.layout.spinner_item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        setTrainStatusObserver()
        setLineChangeObserver()
        setTitleChangedObserver()
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
                        return@with
                    }
                    addAll(it)
                    trainStatusList?.adapter?.notifyDataSetChanged()
                }
            }
        }
        viewModel.currentTrainStatusData.observe(this, trainStatusObserver)
    }

    private fun setLineChangeObserver() {
        val lineChangeObserver = Observer<Int> @Synchronized {
            if (it != null) {
                Timber.d("New line selected: $it")
                parseNewLine(it)
            }
        }

        viewModel.selectedTrainLine.observe(this, lineChangeObserver)
    }

    private fun setTitleChangedObserver() {
        val titleChangedObserver = Observer<String> @Synchronized {
            if (it != null) {
                Timber.d("Direction changed $it")
                statusCollapsing?.title = it
            }
        }

        viewModel.title.observe(this, titleChangedObserver)
    }

    /**
     * Set the direction spinner based on the line number, necessary because some go North-South,
     * some go East-West
     */
    private fun setDirSpinner(lineNum: Int) {

        val array = resources.getStringArray(R.array.line_array)
        var dirArray = R.array.ns_dir_array
        if(array[lineNum] == "Brunswick") {
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
        if(directionSpinner == null) {
            return
        }

        val array = resources.getStringArray(R.array.line_array)

        if( lineNum < 0 || lineNum >= array.size) {
            Timber.d("Invalid array item: $lineNum of ${array.size - 1}")
            return
        }

        setDirSpinner(lineNum)
    }

    /**
     * Initialize the line spinner
     */
    private fun initLineSpinner() {
        val prefs = context?.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE)
        val lastLine = prefs?.getInt(getString(R.string.last_line), 0)
        if (lastLine != null) {
            lineSpinner?.setSelection(lastLine)
            setDirSpinner(lastLine)
        }

        val lineAdapter = ArrayAdapter.createFromResource(context, R.array.line_array, spinnerItem)
        lineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        lineSpinner.adapter = lineAdapter
    }

    private fun initRecyclerView() {
        trainStatusList ?: return
        val viewManager = LinearLayoutManager(context)
        trainStatusList.apply {
            setHasFixedSize(true)
            layoutManager = viewManager

            itemAnimator = DefaultItemAnimator()
            adapter = statusAdapter

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