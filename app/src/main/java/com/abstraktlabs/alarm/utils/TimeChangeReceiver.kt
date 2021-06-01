package com.abstraktlabs.alarm.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.abstraktlabs.alarm.models.DispatcherProvider
import com.abstraktlabs.alarm.repositories.DefaultAlarmRepository

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import javax.inject.Inject

@AndroidEntryPoint
class TimeChangeReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: DefaultAlarmRepository

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    override fun onReceive(context: Context?, intent: Intent?) {

        // Cancel all the active alarms and set them again.
        if (context != null && intent != null) {
            val action = intent.action

            if (action == Intent.ACTION_TIMEZONE_CHANGED || action == Intent.ACTION_TIME_CHANGED || action == Intent.ACTION_DATE_CHANGED) {
                GlobalScope.launch(dispatcherProvider.io) {
                    repository.cancelAlarmsAndSet(context)
                }
            }
        }
    }

}