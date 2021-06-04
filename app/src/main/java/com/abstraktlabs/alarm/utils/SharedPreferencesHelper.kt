package com.abstraktlabs.alarm.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.abstraktlabs.alarm.models.ClockFace

object SharedPreferencesHelper {


    fun changeClockFace(value: ClockFace, context: Context) {

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

        getSharedPref(context).edit {
            putString(Constants.CLOCK_FACE, clock)
            apply()
        }
    }

    fun getClockFace(context: Context): ClockFace =
        ClockFace.from(getSharedPref(context).getString(Constants.CLOCK_FACE, null))


    fun getSharedPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            Constants.SETTINGS_PREF,
            Context.MODE_PRIVATE
        )
    }





}