package jobs;

import entities.Yahoo;
import enums.YahooEnum;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class ScoreJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        Yahoo.getScoreAlerts(YahooEnum.SCORE_UPDATE);
    }
}
