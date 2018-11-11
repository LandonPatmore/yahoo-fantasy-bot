package jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import services.Yahoo;
import shared.YahooEnum;

public class CloseScoreJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        Yahoo.getScoreAlerts(YahooEnum.CLOSE_SCORE_UPDATE);
    }
}
