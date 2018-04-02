package com.idleoffice.marctrain.ui.main

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.WindowManager
import android.widget.Toast
import com.idleoffice.marctrain.BR
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.databinding.ActivityMainBinding
import com.idleoffice.marctrain.ui.alert.AlertFragment
import com.idleoffice.marctrain.ui.base.BaseActivity
import com.idleoffice.marctrain.ui.base.BaseFragment
import com.idleoffice.marctrain.ui.status.StatusFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), MainNavigator, HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val bindingVariable: Int = BR.viewModel
    override val layoutId: Int = R.layout.activity_main
    val backStack = Stack<Int>()

    private var activityMainBinding : ActivityMainBinding? = null
    private var mainViewModel : MainViewModel? = null

    private fun loadFragment(frag: BaseFragment<*,*>, menuItem: Int) {
        if(supportFragmentManager.findFragmentByTag(frag.fragTag) != null) {
            Timber.d("Fragment already exists, skipping.")
            return
        }
        val ft = supportFragmentManager.beginTransaction()
        Timber.d("Replacing fragment view.")
        ft.replace(R.id.view_content, frag, frag.fragTag)
        ft.commit()
        backStack.push(menuItem)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                loadFragment(StatusFragment(), item.itemId)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                loadFragment(AlertFragment(), item.itemId)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
//                message.setText(R.string.title_schedule)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onBackPressed() {
        if(backStack.empty()) {
            super.onBackPressed()
            return
        }
        backStack.pop()
        navigation?.selectedItemId = backStack.pop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("Main activity onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.activityMainBinding = viewDataBinding
        mainViewModel?.navigator = this

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        loadFragment(StatusFragment(), 1)
    }

    override fun displayError(errorMsg: String) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
    }

    override fun getActivityViewModel(): MainViewModel {
        if(mainViewModel == null) {
            mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        }

        return mainViewModel as MainViewModel
    }


    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    override fun onFragmentAttached() {

    }

    override fun onFragmentDetached(tag: String) {
    }
}
