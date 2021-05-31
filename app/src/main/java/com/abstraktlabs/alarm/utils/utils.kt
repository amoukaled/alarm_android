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

import android.util.Log
import java.text.DateFormat
import java.util.*

// Log TAG for easier filtering
private const val tag = "DEBUG/"

/**
 * Logs the instance to the console.
 * Used in tests.
 */
fun logInstance(instance: Any?, payload: String = "") {
    if (payload.isNotBlank()) {
        Log.d(
            "$tag/LoggingInstance",
            "$payload ${instance.toString()}"
        )
    } else {
        Log.d(
            "$tag/LoggingInstance",
            "${instance?.javaClass?.name} is ${instance.toString()}"
        )
    }
}

/**
 * Formats the date and returns a string based on the
 * time format.
 */
fun formatTimeToString(hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }

    return DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)
}