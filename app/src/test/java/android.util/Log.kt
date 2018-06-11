/*
 * Copyright (c) 2018 IdleOffice Inc.
 *
 * Log.kt is part of MarcTrainTracker.
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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package android.util

class Log {
    companion object {
        @JvmStatic
        fun println(priority: Int, tag: String, msg: String): Int {
            val priorityStr = when(priority) {
                7 -> "ASSERT"
                3 -> "DEBUG"
                2 -> "VERBOSE"
                5 -> "WARN"
                6 -> "ERROR"
                4 -> "INFO"
                else -> "NONE"
            }
            System.out.println("$priorityStr: $tag: $msg")
            return 0
        }

        @JvmStatic
        fun println(tag: String, msg: String): Int {
            System.out.println("$tag: $msg")
            return 0
        }
    }
}