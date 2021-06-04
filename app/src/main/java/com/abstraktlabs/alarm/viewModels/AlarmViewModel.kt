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


import android.app.Application
import android.content.Context
import android.content.SharedPreferences

import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.abstraktlabs.alarm.models.ClockFace
import com.abstraktlabs.alarm.models.DispatcherProvider
import com.abstraktlabs.alarm.repositories.DefaultAlarmRepository
import com.abstraktlabs.alarm.room.AlarmEntity
import com.abstraktlabs.alarm.utils.Constants

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

// TODO Check AndroidViewModel
// TODO Check sharedPrefListener issue
// TODO write documentation

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val repo: DefaultAlarmRepository,
    private val dispatchers: DispatcherProvider, application: Application
) : AndroidViewModel(application) {

    init {
        viewModelScope.launch(dispatchers.io) {
            repo.updateAlarms()
        }

        getSharedPref().registerOnSharedPreferenceChangeListener(this@AlarmViewModel::listener.get())
    }

    val alarms: StateFlow<MutableList<AlarmEntity>> = repo.repoAlarms

    // Alarm

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


    // Clock

    private val _clockFace = MutableStateFlow(ClockFace.Stacked)
    val clockFace: StateFlow<ClockFace> = _clockFace

    fun changeClockFace(value: ClockFace) {

        val clock = value.run {
            when (this) {
                ClockFace.Expanded -> {
                    Constants.EXPANDED_CLOCK_FACE
                }

                ClockFace.Stacked -> {
                    Constants.STACKED_CLOCK_FACE
                }
            }
        }

        getSharedPref().edit {
            putString(Constants.CLOCK_FACE, clock)
            apply()
        }
    }

    private fun getClockFace(sharedPreferences: SharedPreferences = getSharedPref()): ClockFace =
        ClockFace.from(sharedPreferences.getString(Constants.CLOCK_FACE, null))


    private fun getSharedPref(): SharedPreferences {
        return getApplication<Application>().getSharedPreferences(
            Constants.SETTINGS_PREF,
            Context.MODE_PRIVATE
        )
    }

    private val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, _ ->
            val value = getClockFace(sharedPreferences)

            this._clockFace.value = value
        }

    override fun onCleared() {
        super.onCleared()
        getSharedPref().unregisterOnSharedPreferenceChangeListener(this@AlarmViewModel::listener.get())
    }
}