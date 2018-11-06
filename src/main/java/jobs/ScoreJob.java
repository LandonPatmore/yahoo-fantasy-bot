package jobs;

import entities.Yahoo;
import enums.YahooEnum;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class ScoreJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        Yahoo.scoreAlert(YahooEnum.SCORE_UPDATE);
    }
}
