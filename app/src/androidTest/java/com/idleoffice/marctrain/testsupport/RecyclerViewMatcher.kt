/*
 * Copyright (c) 2019 IdleOffice Inc.
 *
 * RecyclerViewMatcher.kt is part of MarcTrainTracker.
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

import android.content.res.Resources
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class RecyclerViewMatcher(@IdRes private val recyclerViewId: Int) {

    companion object {
        fun withRecyclerView(@IdRes viewId: Int): RecyclerViewMatcher {
            return RecyclerViewMatcher(viewId)
        }
    }

    /**
     * Find a view at a position in a given recycler view
     */
    fun atPosition(position: Int): Matcher<View> {
        return atPositionOnView(position, -1)
    }

    private fun atPositionOnView(
            position: Int,
            targetViewId: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            var resources: Resources? = null
            var childView: View? = null

            override fun describeTo(description: Description) {
                var idDescription = Integer.toString(recyclerViewId)

                resources?.let {
                    idDescription = try {
                        it.getResourceName(recyclerViewId)
                    } catch (var4: Resources.NotFoundException) {
                        "$recyclerViewId (resource name not found)"
                    }
                }

                description.appendText("RecyclerView with id: $idDescription at position: $position")
            }

            override fun matchesSafely(view: View): Boolean {

                this.resources = view.resources


                if (childView == null) {
                    val recyclerView = view.rootView.findViewById(recyclerViewId) as RecyclerView

                    if (recyclerView.id == recyclerViewId) {
                        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)

                        if (viewHolder != null) {
                            childView = viewHolder.itemView
                        }
                    } else {
                        return false
                    }
                }

                return if (targetViewId == -1) {
                    view === childView
                } else {
                    view === childView?.findViewById<View>(targetViewId)
                }
            }
        }
    }
}