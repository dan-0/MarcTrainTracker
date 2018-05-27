package com.idleoffice.marctrain.ui.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.widget.Toast
import com.idleoffice.marctrain.BR
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.databinding.ActivityMainBinding
import com.idleoffice.marctrain.helpers.vibrateTap
import com.idleoffice.marctrain.ui.alert.AlertFragment
import com.idleoffice.marctrain.ui.base.BaseActivity
import com.idleoffice.marctrain.ui.base.BaseFragment
import com.idleoffice.marctrain.ui.schedule.ScheduleFragment
import com.idleoffice.marctrain.ui.status.StatusFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.architecture.ext.viewModel
import timber.log.Timber

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), MainNavigator {
    override val actViewModel by viewModel<MainViewModel>()
    override val bindingVariable: Int = BR.viewModel
    override val layoutId: Int = R.layout.activity_main

    private fun loadMenuFragment(frag: BaseFragment<*,*>) {
        // Reuse same fragment if it happens to exist already
        val fragment = supportFragmentManager
                .findFragmentByTag(frag.fragTag) as? BaseFragment<*, *> ?: frag

        Timber.d("Replacing fragment view.")
        supportFragmentManager.beginTransaction()
                .replace(R.id.view_content, fragment, fragment.fragTag)
                .commit()
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_status -> {
                vibrateTap()
                loadMenuFragment(StatusFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_alert -> {
                vibrateTap()
                loadMenuFragment(AlertFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_schedule -> {
                vibrateTap()
                loadMenuFragment(ScheduleFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("Main activity onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        actViewModel.navigator = this
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        loadMenuFragment(StatusFragment())
    }

    override fun displayError(errorMsg: String) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
    }
}
