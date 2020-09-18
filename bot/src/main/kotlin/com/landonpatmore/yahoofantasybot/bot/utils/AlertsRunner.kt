/*
 * MIT License
 *
 * Copyright (c) 2020 Landon Patmore
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.landonpatmore.yahoofantasybot.bot.utils

import com.landonpatmore.yahoofantasybot.bot.bridges.ConfigurationBridge
import com.landonpatmore.yahoofantasybot.bot.utils.alerts.CloseScoreAlert
import com.landonpatmore.yahoofantasybot.bot.utils.alerts.MatchUpAlert
import com.landonpatmore.yahoofantasybot.bot.utils.alerts.ScoreAlert
import com.landonpatmore.yahoofantasybot.bot.utils.alerts.StandingsAlert
import com.landonpatmore.yahoofantasybot.bot.utils.models.Configuration
import com.landonpatmore.yahoofantasybot.shared.database.models.Alert
import org.quartz.*
import org.quartz.JobBuilder.newJob
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.impl.StdSchedulerFactory
import org.quartz.impl.matchers.GroupMatcher
import java.util.*


class AlertsRunner(private val configurationBridge: ConfigurationBridge) {

    private val scheduler = StdSchedulerFactory.getDefaultScheduler()

    fun start() {
        scheduler.start()
        configurationBridge.eventStream
            .ofType(Configuration.Alerts::class.java)
            .map {
                it.alerts
            }.subscribe {
                generateJobs(it)
            }
    }

    private fun generateJobs(alerts: List<Alert>) {
        removeJobs(alerts.map { it.uuid })
        alerts.forEach {
            generateCron(it)?.let { cron ->
                createJob(
                    when (it.type) {
                        Alert.SCORE -> ScoreAlert::class.java
                        Alert.CLOSE_SCORE -> CloseScoreAlert::class.java
                        Alert.STANDINGS -> StandingsAlert::class.java
                        Alert.MATCHUP -> MatchUpAlert::class.java
                        else -> null
                    }, it.uuid, cron
                )
            } ?: println("Invalid cron, not creating alert")
        }
    }

    // sec min hour ? start-end day *
    private fun generateCron(alert: Alert): String? {
        val day = getDay(alert.dayOfWeek)

        if (validateMinute(alert.minute) && validateHour(alert.hour) && validateMonth(alert.startMonth) && validateMonth(
                alert.endMonth
            ) && validateDay(day) // TODO: timezone validation once added
        ) {
            return "0 ${alert.minute} ${alert.hour} ? ${alert.startMonth}-${alert.endMonth} $day *"
        }
        return null
    }

    private fun validateMinute(minute: Int): Boolean {
        return minute in 0..59
    }

    private fun validateHour(hour: Int): Boolean {
        return hour in 0..59
    }

    private fun validateMonth(month: Int): Boolean {
        return month in 1..12
    }

    private fun validateDay(day: String?): Boolean {
        return day != null
    }

    private fun getDay(day: Int): String? {
        return when (day) {
            1 -> "SUN"
            2 -> "MON"
            3 -> "TUE"
            4 -> "WED"
            5 -> "THU"
            6 -> "FRI"
            7 -> "SAT"
            else -> null
        }
    }

    private fun removeJobs(uuids: List<String>) {
        val scheduler = StdSchedulerFactory.getDefaultScheduler()
        for (groupName: String in scheduler.jobGroupNames) {
            for (jobKey: JobKey in scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                if(!uuids.contains(jobKey.name)) {
                    scheduler.deleteJob(jobKey)
                }
            }
        }
    }

    /**
     * Creates a job to be scheduled.
     * @param jobClass job class
     * @param cron cron string
     */
    private fun createJob(jobClass: Class<out org.quartz.Job>?, jobKey: String, cron: String) {
        if (jobClass == null) {
            return
        }

        val jobDetail = newJob(jobClass)
            .withIdentity(jobKey)
            .build()

        val trigger = newTrigger()
            .startNow()
            .withSchedule(
                CronScheduleBuilder.cronSchedule(cron)
                    .inTimeZone(TimeZone.getTimeZone("UTC"))
            ).build()

        scheduleJob(Job(jobDetail, trigger))
    }

    /**
     * Runs the jobs that were created.
     */
    private fun scheduleJob(job: Job) {
        try {
            scheduler.scheduleJob(job.jobDetail, job.trigger)
        } catch (e: SchedulerException) {
        }

    }

    private class Job(val jobDetail: JobDetail, val trigger: Trigger)

}