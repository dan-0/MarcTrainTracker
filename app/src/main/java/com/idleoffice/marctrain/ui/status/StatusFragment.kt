package com.idleoffice.marctrain.ui.status

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import com.idleoffice.marctrain.BR
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.data.model.TrainStatus
import com.idleoffice.marctrain.databinding.FragmentStatusBinding
import com.idleoffice.marctrain.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_status.*
import timber.log.Timber
import javax.inject.Inject

class StatusFragment: BaseFragment<FragmentStatusBinding, StatusViewModel>(), StatusNavigator {

    @Inject
    override lateinit var viewModel: StatusViewModel

    override val bindingVariable: Int = BR.viewModel
    override val layoutId: Int = R.layout.fragment_status

    // TODO fix this, its just for a proof of concept
    private val statuses : MutableList<TrainStatus> = mutableListOf()

    private val spinnerItem = R.layout.spinner_item

    private var fragmentStatusBinding : FragmentStatusBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentStatusBinding = viewDataBinding
        viewModel.navigator = this
        setTrainStatusObserver()
        setLineChangeObserver()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initLineSpinner()
        initRecyclerView()
        super.onActivityCreated(savedInstanceState)
    }

    private fun setTrainStatusObserver() {
        val trainStatusObserver = Observer<List<TrainStatus>> @Synchronized {
            if (it != null) {
                Timber.d("New train status received")
                with(statuses) {
                    clear()
                    addAll(it)
                    trainStatusList?.adapter?.notifyDataSetChanged()
                }
            }
        }
        viewModel.trainStatusData.observe(this, trainStatusObserver)
    }

    private fun setLineChangeObserver() {
        val lineChangeObserver = Observer<Int> @Synchronized {
            if (it != null) {
                Timber.d("New line selected: %d", it)
                parseNewLine(it)
            }
        }

        viewModel.selectedTrainLine.observe(this, lineChangeObserver)
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
        directionSpinner.adapter = dirAdapter
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


    // TODO fix, none of this is right
    private fun initRecyclerView() {
        trainStatusList ?: return
        var viewManager = LinearLayoutManager(context)
        trainStatusList.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = StatusAdapter(statuses)

            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(context, viewManager.orientation))
        }

    }

    override fun lineChanged() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}