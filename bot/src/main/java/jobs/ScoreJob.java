package jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import utils.ServicesHandler;
import utils.Yahoo;
import utils.YahooEnum;

public class ScoreJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        final String alert = Yahoo.getScoreAlerts(YahooEnum.SCORE_UPDATE);
        if(alert != null) {
            ServicesHandler.sendMessage(alert);
        }
    }
}
