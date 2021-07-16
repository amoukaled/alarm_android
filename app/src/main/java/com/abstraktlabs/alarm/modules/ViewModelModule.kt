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

package com.abstraktlabs.alarm.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.abstraktlabs.alarm.viewModels.AlarmViewModel
import com.abstraktlabs.alarm.viewModels.AlarmViewModelFactory

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap


@Module
@InstallIn(SingletonComponent::class)
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AlarmViewModel::class)
    abstract fun bindAlarmViewModel(alarmViewModel: AlarmViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: AlarmViewModelFactory): ViewModelProvider.Factory

}