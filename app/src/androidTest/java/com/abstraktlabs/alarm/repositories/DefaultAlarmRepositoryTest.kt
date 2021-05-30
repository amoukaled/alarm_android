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

package com.abstraktlabs.alarm.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest

import com.abstraktlabs.alarm.AndroidTestData
import com.abstraktlabs.alarm.models.DispatcherProvider
import com.abstraktlabs.alarm.room.AlarmDao
import com.abstraktlabs.alarm.utils.logInstance
import com.google.common.truth.Truth.assertThat

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope

import org.junit.Before
import org.junit.Rule
import org.junit.Test

import javax.inject.Inject
import javax.inject.Named


@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class DefaultAlarmRepositoryTest {

    @get: Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_dispatcher")
    lateinit var dispatcher: DispatcherProvider

    private lateinit var scope: TestCoroutineScope

    @Inject
    @Named("test_dao")
    lateinit var dao: AlarmDao

    private lateinit var repo: DefaultAlarmRepository

    @Before
    fun setUp() {
        hiltRule.inject()
        repo = DefaultAlarmRepository(dao)
        scope = TestCoroutineScope(dispatcher.main)
    }


    /**
     * Gets all the alarms in memory, must be empty.
     */
    @Test
    fun getAllAlarms() {
        runBlocking {
            val alarms = repo.getAllAlarms()
            assertThat(alarms.also(::logInstance)).isEmpty()
        }
    }

    /**
     * Adds a new alarm and checks if added to the DB.
     */
    @Test
    fun addAlarmAndCheckDB() {
        runBlocking {
            repo.addAlarm(AndroidTestData.alarm1)
            val alarms = repo.getAllAlarms()
            assertThat(alarms.also(::logInstance)).isNotEmpty()
        }
    }

    /**
     * Adds a new alarm, checks if it is added,
     * then deletes and checks again for successful deletion.
     */
    @Test
    fun addAndDeleteAlarm() {
        runBlocking {
            // Add
            repo.addAlarm(AndroidTestData.alarm1)

            // Check
            val alarms = repo.getAllAlarms()
            assertThat(alarms.also(::logInstance)).isNotEmpty()

            // Delete
            val alarmToDel = alarms[0]
            repo.deleteAlarm(alarmToDel)

            // Check
            val newAlarms = repo.getAllAlarms()
            assertThat(newAlarms.also(::logInstance)).isEmpty()
        }
    }

    /**
     * Adds a new alarm, checks if successful.
     * Change alarm title, and checks if it persisted successfully.
     */
    @Test
    fun addAndUpdateAlarm() {
        runBlocking {
            // Add
            repo.addAlarm(AndroidTestData.alarm1)

            // Check
            val alarms = repo.getAllAlarms()
            assertThat(alarms.also(::logInstance)).isNotEmpty()
            assertThat(alarms[0].title.also(::logInstance)).isEqualTo(AndroidTestData.alarm1.title)

            // Change
            val alarm = alarms[0]
            alarm.title = "Changed"
            repo.updateAlarm(alarm)

            // Check
            val newAlarms = repo.getAllAlarms()
            assertThat(newAlarms.also(::logInstance)).isNotEmpty()
            assertThat(newAlarms[0].title.also(::logInstance)).isEqualTo("Changed")
        }
    }

//    /**
//     * Adds an alarm and checks stateflow for new events.
//     */
//    @Test
//    fun addAlarmAndCheckStateFlow() {
//
//        var event: AlarmEvent<List<AlarmEntity>>? = null
//
//        val job = scope.launch {
//            repo.alarms.collect {
//                event = it
//            }
//        }
//
//        assertThat(event).isNotNull()
//        assertThat(event.also(::logInstance)).isInstanceOf(AlarmEvent.Empty::class.java)
//
//        scope.runBlockingTest {
//            repo.addAlarm(AndroidTestData.alarm1)
//        }
//
//        assertThat(event.also(::logInstance)).isNotNull()
//        assertThat(event?.data.also(::logInstance)).isNotEmpty()
//
//        job.cancel()
//    }
//
//    /**
//     * Adds an alarm and checks stateflow for new events.
//     * Removes the alarms and checks again for Empty event.
//     */
//    @Test
//    fun addAlarmAndCheckStateFlowForEmpty() {
//
//        // Set up
//        var event: AlarmEvent<List<AlarmEntity>>? = null
//        val job = scope.launch {
//            repo.alarms.collect {
//                event = it
//            }
//        }
//        assertThat(event).isNotNull()
//        assertThat(event.also(::logInstance)).isInstanceOf(AlarmEvent.Empty::class.java)
//
//        // Add
//        scope.runBlockingTest {
//            repo.addAlarm(AndroidTestData.alarm1)
//        }
//        // Check
//        assertThat(event.also(::logInstance)).isNotNull()
//        assertThat(event?.data.also(::logInstance)).isNotEmpty()
//
//        // Delete
//        scope.runBlockingTest {
//            val alarm = repo.getAllAlarms()[0]
//            repo.deleteAlarm(alarm)
//        }
//
//        // Check
//        assertThat(event.also(::logInstance)).isInstanceOf(AlarmEvent.Empty::class.java)
//        assertThat(event?.message.also(::logInstance)).isNotNull()
//
//        job.cancel()
//    }
}