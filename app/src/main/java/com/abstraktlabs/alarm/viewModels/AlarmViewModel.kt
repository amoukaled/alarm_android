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
import com.abstraktlabs.alarm.repositories.DefaultAlarmRepository
import com.abstraktlabs.alarm.room.AlarmEntity

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject


@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val repo: DefaultAlarmRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    init {
        viewModelScope.launch(dispatchers.io) {
            repo.updateAlarms()
        }
    }

    val alarms: StateFlow<MutableList<AlarmEntity>> = repo.repoAlarms


    /**
     * Adds and starts the alarm.
     */
    fun addAlarm(
        alarmEntity: AlarmEntity, context: Context,
        scope: CoroutineScope = viewModelScope
    ) {
        scope.launch(dispatchers.io) {
            repo.addAlarm(alarmEntity)
            alarmEntity.startAlarm(context)
        }
    }

    /**
     * Deletes the alarm.
     */
    fun deleteAlarm(alarmEntity: AlarmEntity, scope: CoroutineScope = viewModelScope) {
        scope.launch(dispatchers.io) {
            repo.deleteAlarm(alarmEntity)
        }
    }

    /**
     * Updates the alarm.
     */
    fun updateAlarm(alarmEntity: AlarmEntity, scope: CoroutineScope = viewModelScope) {
        scope.launch(dispatchers.io) {
            repo.updateAlarm(alarmEntity)
        }
    }

    /**
     * Refreshes the flow on the viewModelScope.
     */
    fun refresh() {
        viewModelScope.launch(dispatchers.io) {
            repo.updateAlarms()
        }
    }
}