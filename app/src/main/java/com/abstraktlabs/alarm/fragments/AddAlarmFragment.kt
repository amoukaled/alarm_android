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

package com.abstraktlabs.alarm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels

import com.abstraktlabs.alarm.R
import com.abstraktlabs.alarm.databinding.FragmentAddAlarmBinding
import com.abstraktlabs.alarm.room.AlarmEntity
import com.abstraktlabs.alarm.utils.formatTimeToString
import com.abstraktlabs.alarm.viewModels.AlarmViewModel

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

import dagger.hilt.android.AndroidEntryPoint

import java.util.*

@AndroidEntryPoint
class AddAlarmFragment : BottomSheetDialogFragment() {

    private var binding: FragmentAddAlarmBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddAlarmBinding.inflate(inflater)
        return binding?.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Appropriate styles for modal bottom sheet
        // Adjusts the sheet size take up the minimal space
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheet)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model: AlarmViewModel by activityViewModels()

        val timeFormat =
            if (android.text.format.DateFormat.is24HourFormat(context)) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val currTime = Calendar.getInstance()
        var hour = currTime.get(Calendar.HOUR_OF_DAY)
        var minute = currTime.get(Calendar.MINUTE)

        binding?.apply {
            pickedTimeTV.text = formatTimeToString(hour, minute)

            pickedTimeTV.setOnClickListener {
                // Picker
                val picker =
                    MaterialTimePicker.Builder()
                        .setTimeFormat(timeFormat)
                        .setHour(hour)
                        .setMinute(minute)
                        .setTitleText(resources.getString(R.string.select_alarm_time))
                        .build()

                picker.addOnPositiveButtonClickListener {
                    hour = picker.hour
                    minute = picker.minute
                    this@apply.pickedTimeTV.text = formatTimeToString(hour, minute)
                }

                picker.show(parentFragmentManager, "datePicker")
            }

            addAlarm.setOnClickListener {
                this@apply.titleTV.text?.also {
                    if (it.isNotBlank()) {
                        model.addAlarm(
                            AlarmEntity(it.toString(), hour, minute),
                            activity!!.applicationContext
                        )
                        this@AddAlarmFragment.dismiss()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}