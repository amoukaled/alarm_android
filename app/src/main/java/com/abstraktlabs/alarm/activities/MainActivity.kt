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

import android.content.SharedPreferences
import android.os.Bundle
import android.view.animation.AnimationUtils

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import com.abstraktlabs.alarm.R
import com.abstraktlabs.alarm.adapters.AlarmItemAdapter
import com.abstraktlabs.alarm.databinding.ActivityMainBinding
import com.abstraktlabs.alarm.fragments.AddAlarmFragment
import com.abstraktlabs.alarm.fragments.ChangeClockFragment
import com.abstraktlabs.alarm.fragments.ExpandedClockFragment
import com.abstraktlabs.alarm.fragments.StackedClockFragment
import com.abstraktlabs.alarm.models.ClockFace
import com.abstraktlabs.alarm.utils.SharedPreferencesHelper
import com.abstraktlabs.alarm.viewModels.AlarmViewModel

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var alarmsAdapter: AlarmItemAdapter

    private val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            clockFLInit()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Registering the change listener
        SharedPreferencesHelper.getSharedPref(this)
            .registerOnSharedPreferenceChangeListener(this@MainActivity::listener.get())

        changeClockInit()
        initHint()
        newAlarmInit()

        val model: AlarmViewModel by viewModels()
        clockFLInit()
        alarmsRVInit(model)
        stateFlowCollect(model)
    }

    /**
     * Starts collecting stateFlow events and updates the UI.
     */
    private fun stateFlowCollect(model: AlarmViewModel) {
        lifecycleScope.launchWhenCreated {
            model.alarms.collect { alarms ->
                val placeholder = if (alarms.size == 1) {
                    resources.getString(R.string.alarm, "1")
                } else {
                    val arg = alarms.size.toString()
                    resources.getString(R.string.alarms, arg)
                }
                binding.alarmCountTV.text = placeholder
                alarmsAdapter.updateItemsAndNotify(alarms)
            }
        }
    }

    /**
     * Initializes the AlarmAdapter and the AlarmsRecyclerView.
     * Called before any access to the adapter.
     */
    private fun alarmsRVInit(model: AlarmViewModel) {
        alarmsAdapter = AlarmItemAdapter(model.alarms.value, model)
        with(binding.alarmsRV) {
            adapter = alarmsAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    /**
     * Initializes the Clock FrameLayout.
     */
    private fun clockFLInit() {
        when (SharedPreferencesHelper.getClockFace(this)) {
            ClockFace.Stacked -> {
                supportFragmentManager.beginTransaction().apply {
                    replace(binding.clockFL.id, StackedClockFragment())
                    commit()
                }
            }

            ClockFace.Expanded -> {
                supportFragmentManager.beginTransaction().apply {
                    replace(binding.clockFL.id, ExpandedClockFragment())
                    commit()
                }
            }

        }
    }

    /**
     * Initialized the ChangeClockButton to display a bottom
     * dialog.
     */
    private fun changeClockInit() {
        binding.changeClockButton.setOnClickListener {
            val dialog = ChangeClockFragment()
            dialog.show(supportFragmentManager, "changeClock")
        }
    }

    /**
     * Initializes the New Alarm Button to display a bottom
     * dialog.
     */
    private fun newAlarmInit() {
        binding.newAlarm.setOnClickListener {
            val dialog = AddAlarmFragment()
            dialog.show(supportFragmentManager, "addAlarm")
        }
    }

    /**
     * Initializes the hint ImageButton.
     */
    private fun initHint() {
        with(binding) {
            infoTV.isGone = true

            infoButton.setOnClickListener {
                val scaleUp = AnimationUtils.loadAnimation(this@MainActivity, R.anim.scale_up)
                scaleUp.duration = 100

                val scaleDown = AnimationUtils.loadAnimation(this@MainActivity, R.anim.scale_down)
                scaleDown.duration = 100

                lifecycleScope.launch(Dispatchers.Main) {
                    infoTV.apply {
                        isVisible = true
                        startAnimation(scaleUp)
                        delay(2000)
                        startAnimation(scaleDown)
                        isGone = true
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val model: AlarmViewModel by viewModels()
        model.refresh()
        SharedPreferencesHelper.getSharedPref(this)
            .registerOnSharedPreferenceChangeListener(this@MainActivity::listener.get())
    }

    override fun onPause() {
        super.onPause()
        SharedPreferencesHelper.getSharedPref(this)
            .unregisterOnSharedPreferenceChangeListener(this@MainActivity::listener.get())
    }

    override fun onDestroy() {
        super.onDestroy()
        SharedPreferencesHelper.getSharedPref(this)
            .unregisterOnSharedPreferenceChangeListener(this@MainActivity::listener.get())
    }

}
