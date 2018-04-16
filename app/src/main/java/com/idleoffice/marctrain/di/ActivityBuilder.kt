package com.idleoffice.marctrain.di

import com.idleoffice.marctrain.ui.alert.AlertFragment
import com.idleoffice.marctrain.ui.alert.AlertFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.idleoffice.marctrain.ui.main.MainActivity
import com.idleoffice.marctrain.ui.main.MainActivityModule
import com.idleoffice.marctrain.ui.schedule.ScheduleFragment
import com.idleoffice.marctrain.ui.schedule.ScheduleFragmentModule
import com.idleoffice.marctrain.ui.status.StatusFragment
import com.idleoffice.marctrain.ui.status.StatusFragmentModule


@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindMainActivity() : MainActivity

    @ContributesAndroidInjector(modules = [StatusFragmentModule::class])
    abstract fun bindStatusFragment() : StatusFragment

    @ContributesAndroidInjector(modules = [AlertFragmentModule::class])
    abstract fun bindAlertFragment() : AlertFragment

    @ContributesAndroidInjector(modules = [ScheduleFragmentModule::class])
    abstract fun bindScheduleFragment() : ScheduleFragment
}