package bot.utils

import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder.newJob
import org.quartz.JobDetail
import org.quartz.SchedulerException
import org.quartz.Trigger
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.impl.StdSchedulerFactory
import java.util.*


object JobRunner {
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
            .withSchedule(CronScheduleBuilder.cronSchedule(cron).inTimeZone(TimeZone.getTimeZone("GMT")))
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
            println(e.localizedMessage)
        }

    }

    private class Job(val jobDetail: JobDetail, val trigger: Trigger)

}