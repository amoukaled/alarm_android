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

package com.abstraktlabs.alarm.activities

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import com.abstraktlabs.alarm.databinding.ActivityAlarmSetOffBinding
import com.abstraktlabs.alarm.models.DispatcherProvider
import com.abstraktlabs.alarm.repositories.DefaultAlarmRepository
import com.abstraktlabs.alarm.utils.Constants
import com.abstraktlabs.alarm.utils.formatTimeToString

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch

import javax.inject.Inject

@AndroidEntryPoint
class AlarmSetOffActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: DefaultAlarmRepository

    @Inject
    lateinit var dispatchers: DispatcherProvider

    private lateinit var binding: ActivityAlarmSetOffBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmSetOffBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Getting the alarm id and setting onclick listeners.
        (intent.extras?.get(Constants.alarmId) as Long?)?.let { id ->
            lifecycleScope.launch(dispatchers.io) {

                repository.getAlarmById(id)?.let { alarm ->

                    binding.apply {

                        // TextViews
                        alarmTitle.text = alarm.title
                        alarmTime.text =
                            formatTimeToString(alarm.hour, alarm.minute)

                        // Buttons
                        cancelAlarmButton.setOnClickListener {
                            lifecycleScope.launch(dispatchers.io) {
                                repository.cancelAlarmAfterSetOff(id)
                                finish()
                            }
                        }
                        snoozeAlarmButton.setOnClickListener {
                            lifecycleScope.launch(dispatchers.io) {
                                repository.snoozeAlarmAfterSetOff(id, applicationContext)
                                finish()
                            }
                        }

                    }

                }
            }
        }
    }
}