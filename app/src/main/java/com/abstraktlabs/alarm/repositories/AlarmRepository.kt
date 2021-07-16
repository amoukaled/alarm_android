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

package com.abstraktlabs.alarm.repositories

import android.content.Context
import com.abstraktlabs.alarm.room.AlarmEntity
import kotlinx.coroutines.flow.StateFlow

interface AlarmRepository {

    val alarms: StateFlow<MutableList<AlarmEntity>>

    /**
     * Adds an alarm and updates the instance id.
     * Refreshes the stateflow.
     */
    suspend fun addAlarm(alarmEntity: AlarmEntity)

    /**
     * Deletes an alarm from the DB.
     * Refreshes the stateflow.
     */
    suspend fun deleteAlarm(alarmEntity: AlarmEntity)

    /**
     * Gets all the alarms in the DB.
     */
    suspend fun getAllAlarms(): List<AlarmEntity>

    /**
     * Updates an alarm.
     * Refreshes the stateflow.
     */
    suspend fun updateAlarm(alarmEntity: AlarmEntity)

    /**
     * Gets an alarm by id.
     */
    suspend fun getAlarmById(id: Long): AlarmEntity?

    /**
     * Cancels an alarm after going off.
     */
    suspend fun cancelAlarmAfterSetOff(id: Long)

    /**
     * Snoozes an alarm after going off.
     */
    suspend fun snoozeAlarmAfterSetOff(id: Long, context: Context)

    /**
     * Gets all alarms and emits a new list.
     */
    suspend fun updateAlarms()

    /**
     * Cancels all alarms and re-sets.
     * If the alarm is snoozing it will be cancelled and
     * set accordingly.
     */
    suspend fun cancelAlarmsAndSet(context: Context)

    /**
     * Restarts all active alarms.
     */
    suspend fun restartAllActiveAlarms(context: Context)
}