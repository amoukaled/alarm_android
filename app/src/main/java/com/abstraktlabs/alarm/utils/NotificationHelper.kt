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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService

import com.abstraktlabs.alarm.activities.AlarmSetOffActivity
import com.abstraktlabs.alarm.R


/**
 * Helper class for alarm notification.
 */
object NotificationHelper {

    private const val channelId = "alarmChannel"

    /**
     * Creates a channel.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(this.channelId, "Alarm", NotificationManager.IMPORTANCE_HIGH)


            notificationChannel.apply {
                description = "Alarms"
                setBypassDnd(true)
            }

            val manager = getSystemService(context, NotificationManager::class.java)

            manager?.createNotificationChannel(notificationChannel)
        }
    }

    fun sendNotification(
        context: Context, title: String,
        content: String, alarmId: Long
    ) {
        val notificationManager = NotificationManagerCompat.from(context)

        // On notification click
        val clickIntent = Intent(context, AlarmSetOffActivity::class.java).apply {
            putExtra(Constants.ALARM_ID, alarmId)
        }
        val clickPendingIntent = PendingIntent.getActivity(context, alarmId.toInt(), clickIntent, 0)

        // On notification cancel
        val cancelIntent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(Constants.ALARM_ID, alarmId)
            putExtra(Constants.NOTIFICATION_TASK, NotificationTask.CANCEL)
        }
        val cancelPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            cancelIntent,
            PendingIntent.FLAG_ONE_SHOT
        )

        // On notification Snooze
        val snoozeIntent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(Constants.ALARM_ID, alarmId)
            putExtra(Constants.NOTIFICATION_TASK, NotificationTask.SNOOZE)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            snoozeIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )


        val notification = NotificationCompat.Builder(context, this.channelId)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(title)
            .setContentText(content)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(300000))
            .setLights(Color.RED, 3000, 3000)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setContentIntent(clickPendingIntent)
            .addAction(R.drawable.ic_alarm, "Cancel", cancelPendingIntent)
            .addAction(R.drawable.ic_alarm, "Snooze", snoozePendingIntent)
            .build()

        notificationManager.notify(alarmId.toInt(), notification)
    }

}