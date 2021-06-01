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
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: DefaultAlarmRepository

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    // Restart all alarms after boot
    override fun onReceive(context: Context?, intent: Intent?) {

        if (context != null && intent != null) {
            val action = intent.action

            if (action == Intent.ACTION_BOOT_COMPLETED) {
                GlobalScope.launch(dispatcherProvider.io) {
                    repository.restartAllActiveAlarms(context)
                }
            }
        }
    }
}