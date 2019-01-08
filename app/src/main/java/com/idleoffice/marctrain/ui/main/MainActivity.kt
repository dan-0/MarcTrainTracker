/*
 * Copyright (c) 2018 IdleOffice Inc.
 *
 * MainActivity.kt is part of MarcTrainTracker.
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

package com.idleoffice.marctrain.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.IdRes
import com.idleoffice.marctrain.BR
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.coroutines.CoroutineContextProvider
import com.idleoffice.marctrain.databinding.ActivityMainBinding
import com.idleoffice.marctrain.ui.alert.AlertFragment
import com.idleoffice.marctrain.ui.base.BaseActivity
import com.idleoffice.marctrain.ui.base.BaseFragment
import com.idleoffice.marctrain.ui.schedule.ScheduleFragment
import com.idleoffice.marctrain.ui.status.StatusFragment
import com.idleoffice.marctrain.vibrateTap
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber



class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), MainNavigator, OnBackPressedHandler {


    override val actViewModel by viewModel<MainViewModel>()
    override val bindingVariable: Int = BR.viewModel
    override val layoutId: Int = R.layout.activity_main

    private var backButtonCount = 0
    private val coroutineContextProvider by inject<CoroutineContextProvider>()
    private var job = Job()
    private val mainScope = CoroutineScope(coroutineContextProvider.ui + job)

    private fun loadMenuFragment(frag: BaseFragment<*,*>) {
        // Reuse same fragment if it happens to exist already
        val fragment = supportFragmentManager
                .findFragmentByTag(frag.fragTag) as? BaseFragment<*, *> ?: frag

        Timber.d("Replacing fragment view.")
        supportFragmentManager.beginTransaction()
                .disallowAddToBackStack()
                .replace(R.id.view_content, fragment, fragment.fragTag)
                .commitAllowingStateLoss()
    }

    private fun loadFromId(@IdRes id: Int): Boolean {
        backButtonCount = 0
        return when (id) {
            R.id.navigation_status -> {
                vibrateTap()
                loadMenuFragment(StatusFragment())
                true
            }
            R.id.navigation_alert -> {
                vibrateTap()
                loadMenuFragment(AlertFragment())
                true
            }
            R.id.navigation_schedule -> {
                vibrateTap()
                loadMenuFragment(ScheduleFragment())
                true
            }

            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("Main activity onCreate called")
        backButtonCount = 0
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        actViewModel.navigator = this
    }

    override fun onResume() {
        backButtonCount = 0
        navigation.setOnNavigationItemSelectedListener {
            loadFromId(it.itemId)
        }
        if (!loadFromId(navigation.selectedItemId)) {
            loadMenuFragment(StatusFragment())
        }
        super.onResume()
    }

    override fun onPause() {
        navigation.setOnNavigationItemSelectedListener(null)
        super.onPause()
    }

    override fun displayError(errorMsg: String) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
    }

    override var backButtonReceiver: OnBackPressedListener? = null

    override fun onBackPressed() {
        backButtonReceiver?.let {
            if(it.backButtonPressed()) {
                return
            }
        }

        when (backButtonCount) {
            0 -> {
                backButtonCount++
                vibrateTap()
                Toast.makeText(this, getString(R.string.back_to_exit), Toast.LENGTH_SHORT).show()

                mainScope.launch {
                    delay(3000)
                    backButtonCount = 0
                }
            }
            else -> super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (!job.isCancelled) {
            job.cancel()
        }
        super.onDestroy()
    }
}
