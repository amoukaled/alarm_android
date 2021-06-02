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
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment

import com.abstraktlabs.alarm.databinding.FragmentClockBinding

import java.text.DateFormat
import java.util.*

class ClockFragment : Fragment() {

    private var binding: FragmentClockBinding? = null
    private lateinit var mainHandler: Handler

    private val updateTime = object : Runnable {
        override fun run() {
            updateUI()
            mainHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClockBinding.inflate(layoutInflater)
        mainHandler = Handler(Looper.getMainLooper())
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainHandler.post(updateTime)
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateTime)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateTime)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        mainHandler.removeCallbacks(updateTime)
    }

    private fun updateUI() {
        binding?.let {
            val calendar = Calendar.getInstance()

            it.hoursPB.progress = calendar.get(Calendar.HOUR_OF_DAY).run {
                ((this - 12) * 100) / 12
            }

            it.minutesPB.progress = calendar.get(Calendar.MINUTE).run {
                (this * 100) / 60
            }

            it.secondsPB.progress = calendar.get(Calendar.SECOND).run {
                (this * 100) / 60
            }

            it.clockTV.text = DateFormat.getTimeInstance(DateFormat.LONG).format(calendar.time)
        }
    }
}

// TODO change colors
// TODO Profile app performance
