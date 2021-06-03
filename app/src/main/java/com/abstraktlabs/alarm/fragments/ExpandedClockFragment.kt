package com.abstraktlabs.alarm.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible

import androidx.fragment.app.Fragment
import com.abstraktlabs.alarm.R

import com.abstraktlabs.alarm.databinding.FragmentExpandedClockBinding
import com.abstraktlabs.alarm.utils.strictDoubleDigit

import java.util.*

class ExpandedClockFragment : Fragment() {

    private var binding: FragmentExpandedClockBinding? = null
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
        binding = FragmentExpandedClockBinding.inflate(layoutInflater)
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

            // Updates the textview inside the clock // TODO FIX
            it.hoursTV.text = getHourString(calendar)

            it.minutesTV.text = calendar.get(Calendar.MINUTE).strictDoubleDigit()

            it.secondsTV.text = calendar.get(Calendar.SECOND).strictDoubleDigit()

            it.clockFormatTV.apply {
                android.text.format.DateFormat.is24HourFormat(context).let { result ->
                    if (result) {
                        this.isGone = true
                    } else {
                        this.isVisible = true
                        calendar.get(Calendar.HOUR_OF_DAY).let { hourOfDay ->
                            if (hourOfDay < 11) {
                                this.text = context.resources.getString(R.string.am)
                            } else {
                                this.text = context.resources.getString(R.string.pm)
                            }
                        }
                    }
                }
            }

        }
    }

    /**
     * Checks the hour format and returns the appropriate
     * hour string.
     */
    private fun getHourString(calendar: Calendar) =
        android.text.format.DateFormat.is24HourFormat(context).run {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            if (this) {
                hour.strictDoubleDigit()
            } else {
                when {
                    hour == 0 -> {
                        "12"
                    }
                    hour <= 12 -> {
                        hour.toString()
                    }
                    else -> {
                        val hourPrime = (hour - 12)
                        hourPrime.toString()
                    }
                }
            }
        }
}