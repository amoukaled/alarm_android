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

package com.abstraktlabs.alarm.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils

import androidx.recyclerview.widget.RecyclerView

import com.abstraktlabs.alarm.R
import com.abstraktlabs.alarm.databinding.ActivityMainBinding
import com.abstraktlabs.alarm.databinding.AlarmItemBinding
import com.abstraktlabs.alarm.room.AlarmEntity
import com.abstraktlabs.alarm.utils.formatTimeToString
import com.abstraktlabs.alarm.utils.logInstance
import com.abstraktlabs.alarm.viewModels.AlarmViewModel

import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AlarmItemAdapter(val alarms: MutableList<AlarmEntity>, private val model: AlarmViewModel?) :
    RecyclerView.Adapter<AlarmItemAdapter.AlarmViewHolder>() {

    private lateinit var mainBinding: ActivityMainBinding

    // Impl
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = AlarmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.bind(alarm, this::toggleAlarm, position, this::deleteAlarm)
    }

    override fun getItemCount(): Int = alarms.size

    // Callbacks
    fun updateItemsAndNotify(newAlarms: MutableList<AlarmEntity>) {
        this.alarms.clear()
        this.alarms.addAll(newAlarms)
        this.notifyDataSetChanged()
    }

    private fun toggleAlarm(position: Int, value: Boolean, context: Context) {
        val alarm = alarms[position]
        alarm.isActive = value
        if (value) {
            alarm.startAlarm(context)
        } else {
            alarm.cancelAlarm(context)
        }

        model?.updateAlarm(alarm)
        if (!this::mainBinding.isInitialized.also(::logInstance)) {
            mainBinding = ActivityMainBinding.inflate(LayoutInflater.from(context))
        }

        mainBinding.alarmsRV.post {
            notifyItemChanged(position)
        }
    }

    private fun deleteAlarm(position: Int, context: Context, view: View) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_out)
        animation.duration = 400
        view.startAnimation(animation)
        val alarm = alarms[position]
        alarm.cancelAlarm(context)
        model?.deleteAlarm(alarm)
        notifyItemChanged(position)
    }

    class AlarmViewHolder(private val binding: AlarmItemBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            alarm: AlarmEntity,
            toggleAlarm: (Int, Boolean, Context) -> Unit,
            position: Int,
            deleteAlarm: (Int, Context, View) -> Unit
        ) {
            with(binding) {
                isEnabledToggle.isChecked = alarm.isActive
                alarmTitleTV.text = alarm.title
                alarmTimeTV.text = formatTimeToString(alarm.hour, alarm.minute)
                isEnabledToggle.setOnCheckedChangeListener { _, isChecked ->
                    toggleAlarm(position, isChecked, context)
                }
                root.setOnLongClickListener {

                    MaterialAlertDialogBuilder(context).setTitle("Delete ${alarm.title}?")
                        .setPositiveButton(R.string.delete) { dialog, _ ->
                            deleteAlarm(position, context, root)
                            dialog.cancel()
                        }
                        .setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog.cancel()
                        }
                        .show()
                    return@setOnLongClickListener true
                }
            }
        }
    }
}

