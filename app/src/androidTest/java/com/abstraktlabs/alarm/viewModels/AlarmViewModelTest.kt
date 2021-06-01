package com.abstraktlabs.alarm.viewModels

import android.app.AlarmManager
import android.content.Context

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest

import com.abstraktlabs.alarm.AndroidTestData
import com.abstraktlabs.alarm.models.DispatcherProvider
import com.abstraktlabs.alarm.repositories.DefaultAlarmRepository
import com.abstraktlabs.alarm.room.AlarmDao
import com.abstraktlabs.alarm.room.AlarmEntity
import com.abstraktlabs.alarm.utils.logInstance

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope

import org.junit.Before
import org.junit.Rule

import javax.inject.Inject
import javax.inject.Named

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import java.util.*

@SmallTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
class AlarmViewModelTest {


    // Rules
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    // Variables
    private lateinit var scope: TestCoroutineScope
    private lateinit var repo: DefaultAlarmRepository
    private lateinit var viewModel: AlarmViewModel
    private lateinit var testContext: Context
    private lateinit var alarmManager: AlarmManager

    // Injections
    @Inject
    @Named("test_dispatcher")
    lateinit var dispatcher: DispatcherProvider

    @Inject
    @Named("test_dao")
    lateinit var dao: AlarmDao

    @Before
    fun setUp() {
        hiltRule.inject()

        scope = TestCoroutineScope(dispatcher.main)
        repo = DefaultAlarmRepository(dao)
        viewModel = AlarmViewModel(repo, dispatcher)
        testContext =
            ApplicationProvider.getApplicationContext() //InstrumentationRegistry.getInstrumentation().context
        alarmManager = testContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    /**
     * Checks the flow if empty.
     */
    @Test
    fun checksAlarmsFlow() {
        var flow: List<AlarmEntity>? = null

        val job = scope.launch {
            viewModel.alarms.collect {
                flow = it
            }
        }

        assertThat(flow).isNotNull()
        assertThat(flow).isEmpty()

        job.cancel()
    }

    /**
     * Adds an alarm and checks flow.
     */
    @Test
    fun addAlarmAndCheck() {
        var flow: List<AlarmEntity>? = null

        val job = scope.launch {
            viewModel.alarms.collect {
                flow = it
            }
        }

        assertThat(flow).isEmpty()


        // Add
        scope.runBlockingTest {
            viewModel.addAlarm(AndroidTestData.alarm1, testContext, scope)
        }
        assertThat(flow).isNotEmpty()
        val alarm = flow?.get(0)
        assertThat(alarm).isNotNull()
        assertThat(alarm?.title).isEqualTo(AndroidTestData.alarm1.title)

        // Alarm Check
        val nextAlarm = alarmManager.nextAlarmClock // TODO check null issue

        // Getting the alarm time in millis by accessing the private
        // function AlarmEntity.getAlarmCalendar() and setting it to accessible
        // and casting the result to Calendar
        val alarmTime = alarm!!.run {
            this.javaClass.getDeclaredMethod("getAlarmCalendar").apply {
                isAccessible = true
            }.invoke(alarm) as Calendar
        }.timeInMillis

        assertThat(nextAlarm.triggerTime.also(::logInstance)).isEqualTo(alarmTime.also(::logInstance))
        job.cancel()
    }


}