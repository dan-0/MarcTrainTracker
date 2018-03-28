package com.idleoffice.marctrain.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.idleoffice.marctrain.ui.main.MainActivity
import com.idleoffice.marctrain.ui.main.MainActivityModule
import com.idleoffice.marctrain.ui.status.StatusFragment
import com.idleoffice.marctrain.ui.status.StatusFragmentModule


@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindMainActivity() : MainActivity

    @ContributesAndroidInjector(modules = [StatusFragmentModule::class])
    abstract fun bindStatusFragment() : StatusFragment
}