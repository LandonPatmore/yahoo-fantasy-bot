package jobs;

import entities.Yahoo;
import enums.YahooEnum;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class CloseScoreJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        Yahoo.scoreAlert(YahooEnum.CLOSE_SCORE_UPDATE);
    }
}
