package jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import services.Yahoo;
import shared.YahooEnum;

public class ScoreJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        Yahoo.getScoreAlerts(YahooEnum.SCORE_UPDATE);
    }
}
