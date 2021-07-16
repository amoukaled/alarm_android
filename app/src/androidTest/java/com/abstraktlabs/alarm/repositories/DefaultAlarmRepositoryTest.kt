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
import com.abstraktlabs.alarm.room.AlarmEntity
import com.abstraktlabs.alarm.utils.logInstance
import com.google.common.truth.Truth.assertThat

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest

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
     * Checks if the id is not defaulted to 0.
     */
    @Test
    fun addAlarmAndCheckDB() {
        runBlocking {
            repo.addAlarm(AndroidTestData.alarm1)
            val alarms = repo.getAllAlarms()
            assertThat(alarms.also(::logInstance)).isNotEmpty()
            assertThat(alarms[0].id.also(::logInstance)).isNotEqualTo(0)
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

    /**
     * Adds a new alarm and gets the alarm by id.
     */
    @Test
    fun addAlarmAndGetById() {
        runBlocking {
            // Add
            repo.addAlarm(AndroidTestData.alarm1)

            // Check
            val alarms = repo.getAllAlarms()
            assertThat(alarms.also(::logInstance)).isNotEmpty()

            val alarmAdded = repo.getAlarmById(1)
            assertThat(alarmAdded.also(::logInstance)).isNotNull()
            assertThat(alarmAdded?.title).isEqualTo(AndroidTestData.alarm1.title)
        }
    }


    /**
     * Adds an alarm and checks stateflow for new events.
     */
    @Test
    fun addAlarmAndCheckStateFlow() {

        var flow: List<AlarmEntity>? = null

        val job = scope.launch {
            repo.alarms.collect {
                flow = it
            }
        }

        assertThat(flow).isNotNull()
        assertThat(flow.also(::logInstance)).isEmpty()

        scope.runBlockingTest {
            repo.addAlarm(AndroidTestData.alarm1)
        }

        assertThat(flow.also(::logInstance)).isNotNull()
        assertThat(flow?.also(::logInstance)).isNotEmpty()

        job.cancel()
    }

    /**
     * Adds an alarm and checks stateflow for new events.
     * Removes the alarms and checks again for empty list event.
     */
    @Test
    fun addAlarmAndCheckStateFlowForEmpty() {

        // Set up
        var flow: List<AlarmEntity>? = null
        val job = scope.launch {
            repo.alarms.collect {
                flow = it
            }
        }
        assertThat(flow).isNotNull()
        assertThat(flow.also(::logInstance)).isEmpty()

        // Add
        scope.runBlockingTest {
            repo.addAlarm(AndroidTestData.alarm1)
        }
        // Check
        assertThat(flow.also(::logInstance)).isNotNull()
        assertThat(flow).isNotEmpty()

        // Delete
        scope.runBlockingTest {
            val alarm = repo.getAllAlarms()[0]
            repo.deleteAlarm(alarm)
        }

        // Check
        assertThat(flow.also(::logInstance)).isEmpty()

        job.cancel()
    }

    /**
     * Adds a new alarm and checks for new event.
     * Updates the alarm and checks for new events.
     */
    @Test
    fun addNewAlarmAndUpdate() {

        var flow: List<AlarmEntity>? = null

        val job = scope.launch {
            repo.alarms.collect {
                flow = it
            }
        }

        assertThat(flow).isNotNull()

        // Add
        scope.runBlockingTest {
            repo.addAlarm(AndroidTestData.alarm1)
        }
        assertThat(flow.also(::logInstance)).isNotEmpty()
        assertThat(flow?.get(0)?.title).isEqualTo(AndroidTestData.alarm1.title)

        // Update
        val alarm = flow?.get(0)
        alarm?.title = "Changed"

        scope.runBlockingTest {
            repo.updateAlarm(alarm!!)
        }
        assertThat(flow?.get(0)?.title).isEqualTo("Changed")

        job.cancel()
    }

    /**
     * Cancels the alarm and checks if the isActive property is set
     * correctly to false.
     */
    @Test
    fun createAlarmAndCancel() {
        var flow: List<AlarmEntity>? = null

        val job = scope.launch {
            repo.alarms.collect {
                flow = it
            }
        }

        assertThat(flow).isNotNull()

        // Add
        scope.runBlockingTest {
            repo.addAlarm(AndroidTestData.alarm1)
        }
        assertThat(flow).isNotEmpty()
        assertThat(flow?.get(0)?.isActive).isTrue()

        // Cancel
        scope.runBlockingTest {
            repo.cancelAlarmAfterSetOff(flow?.get(0)?.id!!)
        }
        assertThat(flow?.get(0)?.isActive).isFalse()

        job.cancel()
    }

}