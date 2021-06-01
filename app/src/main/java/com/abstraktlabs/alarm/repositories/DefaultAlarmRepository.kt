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

import com.abstraktlabs.alarm.room.AlarmDao
import com.abstraktlabs.alarm.room.AlarmEntity

import kotlinx.coroutines.flow.MutableStateFlow

import javax.inject.Inject

class DefaultAlarmRepository @Inject constructor(private val dao: AlarmDao) : AlarmRepository {

    val repoAlarms = MutableStateFlow<MutableList<AlarmEntity>>(mutableListOf())

    /**
     * Adds an alarm and updates the instance id.
     * Refreshes the stateflow.
     */
    override suspend fun addAlarm(alarmEntity: AlarmEntity) {
        val id = dao.insertAlarm(alarmEntity)
        alarmEntity.id = id
        updateAlarms()
    }

    /**
     * Deletes an alarm from the DB.
     * Refreshes the stateflow.
     */
    override suspend fun deleteAlarm(alarmEntity: AlarmEntity) {
        dao.deleteAlarm(alarmEntity)
        updateAlarms()
    }

    /**
     * Updates an alarm.
     * Refreshes the stateflow.
     */
    override suspend fun updateAlarm(alarmEntity: AlarmEntity) {
        dao.updateAlarm(alarmEntity)
        updateAlarms()
    }

    /**
     * Gets all the alarms in the DB.
     */
    override suspend fun getAllAlarms(): List<AlarmEntity> = dao.getAllAlarms()

    /**
     * Gets an alarm by id.
     */
    override suspend fun getAlarmById(id: Long): AlarmEntity? = dao.getAlarmById(id)

    /**
     * Cancels an alarm after going off.
     */
    override suspend fun cancelAlarmAfterSetOff(id: Long) {
        this.getAlarmById(id)?.let {
            it.isActive = false
            this.updateAlarm(it)
        }
    }

    /**
     * Snoozes an alarm after going off.
     */
    override suspend fun snoozeAlarmAfterSetOff(id: Long, context: Context) {
        this.getAlarmById(id)?.snoozeAlarm(context)
    }

    /**
     * Gets all alarms and emits a new list.
     */
    suspend fun updateAlarms() {
        val dbAlarms = this.getAllAlarms()
        repoAlarms.emit(dbAlarms.toMutableList())
    }

    /**
     * Cancels all alarms and re-sets.
     * If the alarm is snoozing it will be cancelled and
     * set accordingly.
     */
    suspend fun cancelAlarmsAndSet(context: Context) {
        val alarms = this.getAllAlarms()

        for (alarm in alarms) {
            if (alarm.isActive) {
                alarm.cancelAlarm(context)
                alarm.startAlarm(context)
            }
        }

    }
}