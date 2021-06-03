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

import com.abstraktlabs.alarm.databinding.FragmentStackedClockBinding

import java.text.DateFormat
import java.util.*

class StackedClockFragment : Fragment() {

    private var binding: FragmentStackedClockBinding? = null
    private lateinit var mainHandler: Handler

    /**
     * Runnable that updates the UI and
     * delay one second before running again.
     */
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
        binding = FragmentStackedClockBinding.inflate(layoutInflater)
        mainHandler = Handler(Looper.getMainLooper())
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Post the first update
        mainHandler.post(updateTime)
    }

    // On pause, remove the callback.
    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateTime)
    }

    // On resume, post the runnable.
    override fun onResume() {
        super.onResume()
        mainHandler.post(updateTime)
    }

    // On destroy, remove the callback.
    override fun onDestroy() {
        super.onDestroy()
        binding = null
        mainHandler.removeCallbacks(updateTime)
    }

    /**
     * Updates the UI.
     */
    private fun updateUI() {
        binding?.let {
            val calendar = Calendar.getInstance()

            // Gets the hour of day (24Hour format)
            //  12  = 100% -> 12 hours equals 100% of the circle
            // this = x -> this = HourOfDay
            // x = ((this - 12) * 100) / 12
            // this - 12 to get the 12 hour equivalent
            it.hoursPB.progress = calendar.get(Calendar.HOUR_OF_DAY).run {
                val hour = if (this >= 12) (this - 12) else this
                (hour * 100) / 12
            }

            // Gets the minute
            //  60  = 100% -> 60 minutes equals 100% of the circle
            // this = x -> this = current minute
            // x = (this * 100) / 60
            it.minutesPB.progress = calendar.get(Calendar.MINUTE).run {
                (this * 100) / 60
            }

            // Gets the second
            //  60  = 100% -> 60 seconds equals 100% of the circle
            // this = x -> this = current second
            // x = (this * 100) / 60
            it.secondsPB.progress = calendar.get(Calendar.SECOND).run {
                (this * 100) / 60
            }

            // Updates the textview inside the clock
            it.clockTV.text = DateFormat.getTimeInstance(DateFormat.LONG).format(calendar.time)
        }
    }
}

// TODO change colors
// TODO Profile app performance
