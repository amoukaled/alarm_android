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

package com.abstraktlabs.alarm.viewModels

import android.content.Context

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.abstraktlabs.alarm.models.DispatcherProvider
import com.abstraktlabs.alarm.repositories.AlarmRepository
import com.abstraktlabs.alarm.room.AlarmEntity

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import javax.inject.Inject

class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    init {
        viewModelScope.launch(dispatchers.io) {
            alarmRepository.updateAlarms()
        }
    }

    val alarms: StateFlow<MutableList<AlarmEntity>> = alarmRepository.alarms

    /**
     * Adds and starts the alarm.
     */
    fun addAlarm(
        alarmEntity: AlarmEntity, context: Context,
        scope: CoroutineScope = viewModelScope
    ) {
        scope.launch(dispatchers.io) {
            alarmRepository.addAlarm(alarmEntity)
            alarmEntity.startAlarm(context)
        }
    }

    /**
     * Deletes the alarm.
     */
    fun deleteAlarm(
        alarmEntity: AlarmEntity,
        callback: (() -> Unit)?, scope: CoroutineScope = viewModelScope
    ) {
        // Deletes the alarm and fires the callback if not null.
        // Callback is set in the adapter to notify
        // the adapter when the animation finishes.

        scope.launch(dispatchers.io) {
            alarmRepository.deleteAlarm(alarmEntity)

            callback?.let {
                withContext(dispatchers.main) {
                    it.invoke()
                }
            }
        }
    }

    /**
     * Updates the alarm.
     */
    fun updateAlarm(alarmEntity: AlarmEntity, scope: CoroutineScope = viewModelScope) {
        scope.launch(dispatchers.io) {
            alarmRepository.updateAlarm(alarmEntity)
        }
    }

    /**
     * Refreshes the flow on the viewModelScope.
     */
    fun refresh() {
        viewModelScope.launch(dispatchers.io) {
            alarmRepository.updateAlarms()
        }
    }
}