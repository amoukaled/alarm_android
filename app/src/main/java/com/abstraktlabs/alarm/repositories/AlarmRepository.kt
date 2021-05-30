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

interface AlarmRepository {
    suspend fun addAlarm(alarmEntity: AlarmEntity)
    suspend fun deleteAlarm(alarmEntity: AlarmEntity)
    suspend fun getAllAlarms(): List<AlarmEntity>
    suspend fun updateAlarm(alarmEntity: AlarmEntity)
    suspend fun getAlarmById(id: Long): AlarmEntity?
    suspend fun cancelAlarmAfterSetOff(id: Long)
    suspend fun snoozeAlarmAfterSetOff(id: Long, context: Context)
}