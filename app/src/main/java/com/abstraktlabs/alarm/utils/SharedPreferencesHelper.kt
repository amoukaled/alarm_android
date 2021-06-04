/* Copyright (C) 2021  Ali Moukaled
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.abstraktlabs.alarm.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

import com.abstraktlabs.alarm.models.ClockFace

object SharedPreferencesHelper {

    /**
     * Changes the clockFace in SharedPref
     */
    fun changeClockFace(value: ClockFace, context: Context) {
        val clock = value.run {
            when (this) {
                ClockFace.Expanded -> {
                    Constants.EXPANDED_CLOCK_FACE
                }

                ClockFace.Stacked -> {
                    Constants.STACKED_CLOCK_FACE
                }
            }
        }

        getSharedPref(context).edit {
            putString(Constants.CLOCK_FACE, clock)
            apply()
        }
    }

    /**
     * Gets the saved ClockFace in SharedPref.
     */
    fun getClockFace(context: Context): ClockFace =
        ClockFace.from(getSharedPref(context).getString(Constants.CLOCK_FACE, null))

    /**
     * Gets the Shard Pref
     */
    fun getSharedPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            Constants.SETTINGS_PREF,
            Context.MODE_PRIVATE
        )
    }
}