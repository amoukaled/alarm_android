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

import androidx.core.app.NotificationManagerCompat

import com.abstraktlabs.alarm.models.DispatcherProvider
import com.abstraktlabs.alarm.repositories.DefaultAlarmRepository

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import javax.inject.Inject

enum class NotificationTask {
    CANCEL,
    SNOOZE
}

@AndroidEntryPoint
class NotificationReceiver :
    BroadcastReceiver() {

    @Inject
    lateinit var repo: DefaultAlarmRepository

    @Inject
    lateinit var dispatchers: DispatcherProvider

    /**
     * Receives the broadcast and checks for the notification
     * action.
     * Cancels the notification after pressing either buttons.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {

            intent.extras?.also { extras ->
                (extras[Constants.NOTIFICATION_TASK] as NotificationTask?)?.let { task ->
                    (extras[Constants.ALARM_ID] as Long?)?.let { id ->
                        val manager = NotificationManagerCompat.from(context)

                        when (task) {
                            NotificationTask.CANCEL -> {
                                GlobalScope.launch(dispatchers.io) {
                                    repo.cancelAlarmAfterSetOff(id)
                                    manager.cancel(id.toInt())
                                }
                            }
                            NotificationTask.SNOOZE -> {
                                GlobalScope.launch(dispatchers.io) {
                                    repo.snoozeAlarmAfterSetOff(id, context)
                                    manager.cancel(id.toInt())
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}