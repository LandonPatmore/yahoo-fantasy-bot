package jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import services.Yahoo;
import shared.YahooEnum;

public class WeeklyUpdateJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        Yahoo.getScoreAlerts(YahooEnum.WEEKLY_UPDATE);
    }
}
