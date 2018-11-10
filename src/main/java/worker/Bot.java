package worker;

import jobs.CloseScoreJob;
import jobs.ScoreJob;
import jobs.TransactionsJob;
import jobs.WeeklyUpdateJob;
import utils.JobRunner;
import utils.Log;
import utils.Postgres;
import utils.ServicesHandler;

public class Bot {
    private static final Log log = new Log(Bot.class);

    public static void main(String[] args) {
        startServices();
    }


    private static void startServices() {
        // Keep trying to get the latest token
        while (Postgres.getLatestTokenData() == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getLocalizedMessage(), true);
            }
        }

        // Check services for validity
        ServicesHandler.startupCheck();

        // Every 15 Seconds
        JobRunner.createJob(TransactionsJob.class, "0/15 * * ? 9-1 * *");

        // Thursdays at 7:30pm
        JobRunner.createJob(WeeklyUpdateJob.class, "0 30 19 ? 9-1 THU *");

        // Multiple Times
        JobRunner.createJob(ScoreJob.class, "0 30 23 ? 9-1 THU *"); // Thursdays at 11:30pm
        JobRunner.createJob(ScoreJob.class, "0 00 16 ? 9-1 SUN *"); // Sundays at 4:00pm
        JobRunner.createJob(ScoreJob.class, "0 00 20 ? 9-1 SUN *"); // Sundays at 8:00pm
        JobRunner.createJob(ScoreJob.class, "0 30 23 ? 9-1 SUN *"); // Sundays at 11:30pm
        JobRunner.createJob(ScoreJob.class, "0 30 23 ? 9-1 MON *"); // Mondays at 11:30pm

        // Mondays at 7:30pm
        JobRunner.createJob(CloseScoreJob.class, "0 30 19 ? 9-1 MON *");

        JobRunner.runJobs();

    }
}
