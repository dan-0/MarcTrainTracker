package com.idleoffice.marctrain.ui.main

import android.app.Application
import com.idleoffice.marctrain.rx.SchedulerProvider
import com.idleoffice.marctrain.ui.base.BaseViewModel
import java.util.*


class MainViewModel(
        app : Application,
        schedulerProvider: SchedulerProvider)
    : BaseViewModel<MainNavigator>(app, schedulerProvider) {

    companion object {
        const val BACKSTACK_EMPTY = -1
    }

    private val backStack = Stack<Int>()
    var isFragmentLoaded = false
    
    fun addToBackstack(menuItem: Int) {
        if(backStack.size > 10) {
            val new = ArrayList<Int>()
            new.addAll(backStack.subList(backStack.size - 10, backStack.size - 1))
            backStack.clear()
            backStack.add(0)
            backStack.addAll(new)
        }
        backStack.push(menuItem)

        isFragmentLoaded = true
    }

    fun backStackGetLast() : Int {
        if (backStack.isEmpty()) {
            return BACKSTACK_EMPTY
        }

        // Removing the top item from the stack
        backStack.pop()

        // Must check if is empty, otherwise exception could be thrown
        if (backStack.isEmpty()) {
            return BACKSTACK_EMPTY
        }
        return backStack.pop()
    }
}