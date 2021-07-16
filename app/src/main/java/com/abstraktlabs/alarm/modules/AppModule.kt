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

import android.content.Context
import androidx.room.Room

import com.abstraktlabs.alarm.models.DispatcherProvider
import com.abstraktlabs.alarm.repositories.AlarmRepository
import com.abstraktlabs.alarm.repositories.DefaultAlarmRepository
import com.abstraktlabs.alarm.room.AlarmDao
import com.abstraktlabs.alarm.room.AlarmDatabase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesAlarmDB(@ApplicationContext context: Context): AlarmDatabase =
        Room.databaseBuilder(context, AlarmDatabase::class.java, "AlarmDB").build()

    @Provides
    @Singleton
    fun providesAlarmDao(db: AlarmDatabase): AlarmDao = db.alarmDao()

    @Provides
    @Singleton
    fun providesAlarmRepo(dao: AlarmDao): AlarmRepository {
        return DefaultAlarmRepository(dao)
    }

    @Provides
    @Singleton
    fun providesDispatchProvider(): DispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }
}