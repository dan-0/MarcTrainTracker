package com.idleoffice.marctrain.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.idleoffice.marctrain.ui.main.MainActivity
import com.idleoffice.marctrain.ui.main.MainActivityModule


@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindMainActivity() : MainActivity
}