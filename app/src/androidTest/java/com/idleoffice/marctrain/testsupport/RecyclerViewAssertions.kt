package com.idleoffice.marctrain.testsupport

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matchers


object RecyclerViewAssertions {
    fun isEmpty(): ViewAssertion {
        return RecyclerViewCountAssertion(0)
    }

    fun hasElementCount(count: Int): ViewAssertion {
        return RecyclerViewCountAssertion(count)
    }

    private class RecyclerViewCountAssertion(private val count: Int) : ViewAssertion {
        override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            val recyclerView = view as RecyclerView
            ViewMatchers.assertThat(recyclerView.adapter?.itemCount, Matchers.`is`(count))
        }

    }
}