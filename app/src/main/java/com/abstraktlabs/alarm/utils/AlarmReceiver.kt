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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * The broadcast receiver for the alarm event.
 */
class AlarmReceiver : BroadcastReceiver() {

    /**
     * Triggers an alarm notification.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {

            intent.extras?.also {
                val alarmTitle = it[Constants.alarmTitle] as String?
                val alarmId = it[Constants.alarmId] as Long?
                val alarmHour = it[Constants.alarmHour] as Int?
                val alarmMinute = it[Constants.alarmMinute] as Int?

                if (alarmTitle != null && alarmId != null && alarmHour != null && alarmMinute != null) {
                    val alarmTime = formatTimeToString(alarmHour, alarmMinute)
                    NotificationHelper.sendNotification(context, alarmTitle, alarmTime, alarmId)
                }
            }
        }
    }

    // todo on doze mode

}