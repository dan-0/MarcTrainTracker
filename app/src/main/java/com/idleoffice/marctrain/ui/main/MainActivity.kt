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
import com.idleoffice.marctrain.ui.main.MainViewModel.Companion.BACKSTACK_EMPTY
import com.idleoffice.marctrain.ui.schedule.ScheduleFragment
import com.idleoffice.marctrain.ui.status.StatusFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.architecture.ext.viewModel
import timber.log.Timber

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), MainNavigator {
    override val actViewModel by viewModel<MainViewModel>()
    override val bindingVariable: Int = BR.viewModel
    override val layoutId: Int = R.layout.activity_main


    private var activityMainBinding : ActivityMainBinding? = null

    private fun loadFragment(frag: BaseFragment<*,*>, menuItem: Int) {

        if(supportFragmentManager.findFragmentByTag(frag.fragTag) != null) {
            Timber.d("Fragment already exists, skipping.")
            return
        }
        val ft = supportFragmentManager.beginTransaction()
        Timber.d("Replacing fragment view.")
        ft.replace(R.id.view_content, frag, frag.fragTag)
        ft.commit()

        actViewModel.addToBackstack(menuItem)

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {

            R.id.navigation_home -> {
                vibrateTap(this)
                loadFragment(StatusFragment(), item.itemId)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                vibrateTap(this)
                loadFragment(AlertFragment(), item.itemId)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                vibrateTap(this)
                loadFragment(ScheduleFragment(), item.itemId)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onBackPressed() {

        val backStackItem = actViewModel.backStackGetLast()

        if (backStackItem == BACKSTACK_EMPTY) {
            navigation?.selectedItemId = R.id.navigation_home
            super.onBackPressed()
            return
        } else {
            navigation?.selectedItemId = backStackItem
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("Main activity onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.activityMainBinding = viewDataBinding
        actViewModel.navigator = this

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        if (!actViewModel.isFragmentLoaded) {
            loadFragment(StatusFragment(), R.id.navigation_home)
        }
    }

    override fun displayError(errorMsg: String) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
    }

    override fun onFragmentAttached() {

    }

    override fun onFragmentDetached(tag: String) {
    }
}
