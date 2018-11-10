package utils;

import services.Yahoo;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.TimeZone;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class JobRunner {
    private static final Log log = new Log(Yahoo.class);

    private static final ArrayList<Job> jobs = new ArrayList<>();

    /**
     * Creates a job to be scheduled.
     * @param jobClass job class
     * @param cron cron string
     */
    public static void createJob(Class<? extends org.quartz.Job> jobClass, String cron) {
        final JobDetail jobDetail = newJob(jobClass).build();

        final Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(cron).inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        jobs.add(new Job(jobDetail, trigger));
    }

    /**
     * Runs the jobs that were created.
     */
    public static void runJobs() {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            scheduler.start();

            for (Job j : jobs) {
                scheduler.scheduleJob(j.getJobDetail(), j.getTrigger());
            }

        } catch (SchedulerException e) {
            log.error(e.getLocalizedMessage(), true);
        }
    }

    private static class Job {
        private final JobDetail jobDetail;
        private final Trigger trigger;

        public Job(JobDetail jobDetail, Trigger trigger) {
            this.jobDetail = jobDetail;
            this.trigger = trigger;
        }

        public JobDetail getJobDetail() {
            return jobDetail;
        }

        public Trigger getTrigger() {
            return trigger;
        }
    }

}
