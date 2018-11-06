import jobs.TransactionsJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import utils.Log;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class Main {
    private static final Log log = new Log(Main.class);

    public static void main(String[] args) throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        scheduler.start();

        JobDetail transactionJob = newJob(TransactionsJob.class).build();

        Trigger transactionTrigger = newTrigger()
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0/15 * * ? 9-1 * 2018-2019"))
                .build();

        scheduler.scheduleJob(transactionJob, transactionTrigger);

//        JobDetail weeklyJob = newJob(WeeklyUpdateJob.class).build();
//
//        Trigger weeklyTrigger = newTrigger()
//                .startNow()
//                .withSchedule(CronScheduleBuilder.cronSchedule("0 30 19 ? 9-1 THU 2018-2019"))
//                .build();
//
//        scheduler.scheduleJob(weeklyJob, weeklyTrigger);
    }
}
