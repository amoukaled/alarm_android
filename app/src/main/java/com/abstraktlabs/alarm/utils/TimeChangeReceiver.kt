package com.abstraktlabs.alarm.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimeChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (context != null && intent != null) {
            val action = intent.action
            logInstance(intent, "Intent is")
            TODO()
        }
    }

}