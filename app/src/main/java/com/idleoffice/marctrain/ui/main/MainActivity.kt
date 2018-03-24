package com.idleoffice.marctrain.ui.main

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.widget.Toast
import com.idleoffice.marctrain.BR
import kotlinx.android.synthetic.main.activity_main.*
import com.idleoffice.marctrain.R
import com.idleoffice.marctrain.databinding.ActivityMainBinding
import com.idleoffice.marctrain.ui.base.BaseActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), MainNavigator, HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val bindingVariable: Int = BR.viewModel
    override val layoutId: Int = R.layout.activity_main

    private var activityMainBinding : ActivityMainBinding? = null
    var mainViewModel : MainViewModel? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_status)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.title_alerts)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                message.setText(R.string.title_schedule)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("Main activity onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.activityMainBinding = viewDataBinding
        mainViewModel?.navigator = this

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
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
}
