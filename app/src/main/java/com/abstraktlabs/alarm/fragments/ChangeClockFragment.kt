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
import com.abstraktlabs.alarm.databinding.FragmentChangeClockBinding
import com.abstraktlabs.alarm.models.ClockFace
import com.abstraktlabs.alarm.viewModels.AlarmViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChangeClockFragment : BottomSheetDialogFragment() {

    private var binding: FragmentChangeClockBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeClockBinding.inflate(layoutInflater)
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

        val viewModel: AlarmViewModel by activityViewModels()

        initializeRadioButtons(viewModel)

        radioGroupCallback(viewModel)
    }

    /**
     * Adds a callback to the RadioGroup.
     * Updates the SharedPref and dismiss.
     */
    private fun radioGroupCallback(viewModel: AlarmViewModel) {
        binding?.apply {
            clocksRG.setOnCheckedChangeListener { _, checkedId ->

                when (checkedId) {

                    expandedRB.id -> {
                        viewModel.changeClockFace(ClockFace.Expanded)
                        this@ChangeClockFragment.dismiss()
                    }

                    stackedRB.id -> {
                        viewModel.changeClockFace(ClockFace.Stacked)
                        this@ChangeClockFragment.dismiss()

                    }

                    else -> {
                        this@ChangeClockFragment.dismiss()
                    }

                }

            }
        }
    }

    /**
     * Initializes the radio buttons depending on selected clockFace.
     */
    private fun initializeRadioButtons(viewModel: AlarmViewModel) {
        viewModel.clockFace.value.let {
            when (it) {

                ClockFace.Stacked -> {
                    binding?.apply {
                        stackedRB.isChecked = true
                        expandedRB.isChecked = false
                    }
                }

                ClockFace.Expanded -> {
                    binding?.apply {
                        stackedRB.isChecked = false
                        expandedRB.isChecked = true
                    }
                }

            }
        }
    }


}