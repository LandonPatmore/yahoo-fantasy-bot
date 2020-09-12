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

package bot.utils

import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder.newJob
import org.quartz.JobDetail
import org.quartz.SchedulerException
import org.quartz.Trigger
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.impl.StdSchedulerFactory
import java.util.*


class JobRunner {
    private val jobs = ArrayList<Job>()

    /**
     * Creates a job to be scheduled.
     * @param jobClass job class
     * @param cron cron string
     */
    fun createJob(jobClass: Class<out org.quartz.Job>, cron: String) {
        val jobDetail = newJob(jobClass).build()

        val trigger = newTrigger()
            .startNow()
            .withSchedule(
                CronScheduleBuilder.cronSchedule(cron)
                    .inTimeZone(TimeZone.getTimeZone("UTC"))
            )
            .build()

        jobs.add(Job(jobDetail, trigger))
    }

    /**
     * Runs the jobs that were created.
     */
    fun runJobs() {
        try {
            val scheduler = StdSchedulerFactory.getDefaultScheduler()

            scheduler.start()

            for (j in jobs) {
                scheduler.scheduleJob(j.jobDetail, j.trigger)
            }

        } catch (e: SchedulerException) {
            println(e.message)
        }

    }

    private class Job(val jobDetail: JobDetail, val trigger: Trigger)

}