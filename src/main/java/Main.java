import entities.GroupMe;
import jobs.CloseScoreJob;
import jobs.ScoreJob;
import jobs.TransactionsJob;
import jobs.WeeklyUpdateJob;
import utils.JobRunner;

public class Main {
    public static void main(String[] args) {

        // Send startup messages if applicable
        GroupMe.startupMessages();

        // Every 15 Seconds
        JobRunner.createJob(TransactionsJob.class, "0/15 * * ? 9-1 * 2018-2019");

        // Thursdays at 7:30pm
        JobRunner.createJob(WeeklyUpdateJob.class, "0 30 19 ? 9-1 THU 2018-2019");

        // Multiple Times
        JobRunner.createJob(ScoreJob.class, "0 30 23 ? 9-1 THU 2018-2019"); // Thursdays at 11:30pm
        JobRunner.createJob(ScoreJob.class, "0 00 16 ? 9-1 SUN 2018-2019"); // Sundays at 4:00pm
        JobRunner.createJob(ScoreJob.class, "0 00 20 ? 9-1 SUN 2018-2019"); // Sundays at 8:00pm
        JobRunner.createJob(ScoreJob.class, "0 30 23 ? 9-1 SUN 2018-2019"); // Sundays at 11:30pm
        JobRunner.createJob(ScoreJob.class, "0 30 23 ? 9-1 MON 2018-2019"); // Mondays at 11:30pm

        // Mondays at 7:30pm
        JobRunner.createJob(CloseScoreJob.class, "0 30 19 ? 9-1 MON 2018-2019");

        JobRunner.runJobs();
    }
}
