import jobs.CloseScoreJob;
import jobs.ScoreJob;
import jobs.TransactionsJob;
import jobs.WeeklyUpdateJob;
import services.Yahoo;
import utils.JobRunner;
import utils.Log;
import utils.Postgres;
import utils.ServicesHandler;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {
    private static final Log log = new Log(Main.class);

    public static void main(String[] args) {
        port(getHerokuAssignedPort());

        startServices();

        get("/", (req, res) -> {
            if (Postgres.getLatestTokenData() == null) {
                res.redirect(Yahoo.authenticationUrl(req.scheme() + "://" + req.host()));
                return null;
            } else {
                return "You are already authenticated with Yahoo's servers.";
            }
        });

        get("/auth", (req, res) -> {
            // Trade the Request Token and Verfier for the Access Token
            log.trace("Trading request token for access token...", false);
            Yahoo.setToken(Yahoo.getService().getAccessToken(req.queryParams("code")));
            Yahoo.saveAuthenticationData();
            log.trace("Access token received.  Authorized successfully.", false);
            startServices();
            return "You are authorized";
        });
    }


    private static void startServices() {
        if (Postgres.getLatestTokenData() != null) {
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

    private static int getHerokuAssignedPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
