package com.abstraktlabs.alarm.fragments

import android.graphics.ColorFilter
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.abstraktlabs.alarm.databinding.FragmentClockBinding

class ClockFragment : Fragment() {

    private var binding: FragmentClockBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClockBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {

            secondsPB.apply {
                isIndeterminate = false
                max = 100
                progress = 20
                this.indeterminateDrawable.colorFilter = ColorFilter()
            }

            minutesPB.apply {
                isIndeterminate = false
                max = 100
                progress = 80
            }

            hoursPB.apply {
                isIndeterminate = false
                max = 100
                progress = 60
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


}