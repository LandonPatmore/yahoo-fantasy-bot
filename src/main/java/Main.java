import services.Yahoo;
import jobs.CloseScoreJob;
import jobs.ScoreJob;
import jobs.TransactionsJob;
import jobs.WeeklyUpdateJob;
import utils.JobRunner;
import utils.ServicesHandler;

public class Main {
    public static void main(String[] args) {

        // Check services for validity
        ServicesHandler.startupCheck();

        // When the app starts, check if there actually is a Oauth token, else prompt user to get it
        Yahoo.authenticate();

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
