package com.idleoffice.marctrain.ui.schedule

import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.databinding.FragmentScheduleBinding
import com.idleoffice.marctrain.ui.base.BaseFragment
import java.io.File

class ScheduleFragment :
        BaseFragment<FragmentScheduleBinding, ScheduleViewModel>(), ScheduleNavigator {
    override var appContentResolver : ContentResolver? = null
    override var appFilesDir : File? = null
    override var appAssets : AssetManager? = null
    override var appContext : Context? = null


    override val layoutId: Int = R.layout.fragment_schedule

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.navigator = this
        appContentResolver = activity?.contentResolver
        appFilesDir = context?.filesDir
        appAssets = context?.assets
        appContext = context
        retainInstance = true
    }

    override fun displayActivityNotFound() {
        if (view != null) {
            Snackbar.make(view!!, R.string.pdf_reader_unavailable, Snackbar.LENGTH_LONG).show()
        }
    }
}