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

package com.abstraktlabs.alarm.room

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

import com.abstraktlabs.alarm.utils.AlarmReceiver
import com.abstraktlabs.alarm.utils.Constants

import java.util.*

@Entity
data class AlarmEntity(
    @ColumnInfo(name = "Title")
    var title: String,

    @ColumnInfo(name = "Hour")
    var hour: Int,

    @ColumnInfo(name = "Minute")
    var minute: Int,

    @ColumnInfo(name = "isActive")
    var isActive: Boolean = true
) {


    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    override fun toString(): String {
        return "AlarmEntity(id=$id, title=$title, hour=$hour, minute=$minute, isActive=$isActive)"
    }

    @Ignore
    private val snoozeTime = 5 * 60 * 1000

    /**
     * Gets the calendar from the hour and minute properties.
     */
    private fun getAlarmCalendar(): Calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }


    /**
     * Gets the PendingIntent Broadcast for the AlarmEntity
     */
    private fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(Constants.alarmTitle, this@AlarmEntity.title)
            putExtra(Constants.alarmId, this@AlarmEntity.id)
            putExtra(Constants.alarmHour, this@AlarmEntity.hour)
            putExtra(Constants.alarmMinute, this@AlarmEntity.minute)
        }
        return PendingIntent.getBroadcast(context, this@AlarmEntity.id.toInt(), intent, 0)
    }

    /**
     * Starts the alarm.
     */
    fun startAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmTime = this.getAlarmCalendar().timeInMillis
        val now = Calendar.getInstance().timeInMillis

        // If the time has passed, assign it to the next day
        val time = if (now > alarmTime) alarmTime + 86400000 else alarmTime

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            time,
            this.getPendingIntent(context)
        )
    }

    /**
     * Cancels the alarm.
     */
    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(this.getPendingIntent(context))
    }

    /**
     * Snoozes the alarm.
     */
    fun snoozeAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val snoozeFiveMinutes = Calendar.getInstance().timeInMillis + this.snoozeTime

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            snoozeFiveMinutes,
            this.getPendingIntent(context)
        )
    }
}