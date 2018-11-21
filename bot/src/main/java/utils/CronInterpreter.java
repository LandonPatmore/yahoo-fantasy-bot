package utils;

import jobs.CloseScoreJob;
import jobs.ScoreJob;
import jobs.TransactionsJob;
import jobs.WeeklyUpdateJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronScheduleBuilder;

public class CronInterpreter {
    private static final Logger log = LogManager.getLogger(CronInterpreter.class);

    private static final String weeklyUpdateCron = EnvHandler.WEEKLY_UPDATE_CRON.getValue();
    private static final String scoreUpdateCron = EnvHandler.SCORE_UPDATE_CRON.getValue();
    private static final String closeScoreUpdateCron = EnvHandler.CLOSE_SCORE_UPDATE_CRON.getValue();

    public static void interpret() {
        checkCronStrings();
        JobRunner.createJob(TransactionsJob.class, "0/15 * * ? 9-1 * *");
    }

    private static void checkCronStrings() {
        if (weeklyUpdateCron == null) {
            log.debug("Weekly Update Cron is null.  Falling back to default cron: 0 30 19 ? 9-1 THU *");
            defaultWeeklyJob();
        } else {
            try {
                CronScheduleBuilder.cronSchedule(weeklyUpdateCron);
                log.debug("Weekly Update Cron: " + weeklyUpdateCron + " is valid.");
                JobRunner.createJob(WeeklyUpdateJob.class, weeklyUpdateCron);
            } catch (RuntimeException e) {
                log.debug("Weekly Update Cron: " + weeklyUpdateCron + " is invalid.  Falling back to default cron: 0 30 19 ? 9-1 THU *");
                defaultWeeklyJob();
            }
        }

        if (scoreUpdateCron == null) {
            log.debug("Score Update Cron is null.  Falling back to default cron list:\n0 30 23 ? 9-1 THU *,\n0 00 16 ? 9-1 SUN *,\n0 00 20 ? 9-1 SUN *,\n0 30 23 ? 9-1 SUN *,\n0 30 23 ? 9-1 MON *");
            defaultScoreJobs();
        } else {
            final String[] cronList = scoreUpdateCron.split(",");

            for (String cron : cronList) {
                try {
                    CronScheduleBuilder.cronSchedule(cron);
                } catch (RuntimeException e) {
                    log.debug("Score Update Cron: " + cron + " is invalid.  Falling back to default cron list:\n0 30 23 ? 9-1 THU *,\n0 00 16 ? 9-1 SUN *,\n0 00 20 ? 9-1 SUN *,\n0 30 23 ? 9-1 SUN *,\n0 30 23 ? 9-1 MON *");
                    defaultScoreJobs();
                    break;
                }
            }

            log.debug("Score Update Cron: " + scoreUpdateCron + " is valid.");

            for (String cron : cronList) {
                JobRunner.createJob(ScoreJob.class, cron);
            }
        }

        if (closeScoreUpdateCron == null) {
            log.debug("Close Score Update Cron is null.  Falling back to default cron: 0 30 19 ? 9-1 MON *");
            defaultCloseScoreJob();
        } else {
            try {
                CronScheduleBuilder.cronSchedule(closeScoreUpdateCron);
                log.debug("Close Score Update Cron: " + closeScoreUpdateCron + " is valid.");
                JobRunner.createJob(CloseScoreJob.class, closeScoreUpdateCron);
            } catch (RuntimeException e) {
                log.debug("Close Score Update Cron: " + closeScoreUpdateCron + " is invalid.  Falling back to default cron: 0 30 19 ? 9-1 MON *");
                defaultCloseScoreJob();
            }
        }
    }

    private static void defaultWeeklyJob() {
        JobRunner.createJob(WeeklyUpdateJob.class, "0 30 19 ? 9-1 THU *");
    }

    private static void defaultScoreJobs() {
        JobRunner.createJob(ScoreJob.class, "0 30 23 ? 9-1 THU *");
        JobRunner.createJob(ScoreJob.class, "0 00 16 ? 9-1 SUN *");
        JobRunner.createJob(ScoreJob.class, "0 00 20 ? 9-1 SUN *");
        JobRunner.createJob(ScoreJob.class, "0 30 23 ? 9-1 SUN *");
        JobRunner.createJob(ScoreJob.class, "0 30 23 ? 9-1 MON *");
    }

    private static void defaultCloseScoreJob() {
        JobRunner.createJob(CloseScoreJob.class, "0 30 19 ? 9-1 MON *");
    }
}
