/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * RecyclerViewAssertions.kt is part of MarcTrainTracker.
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
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

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