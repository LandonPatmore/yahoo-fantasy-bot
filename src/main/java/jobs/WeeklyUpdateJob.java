package jobs;

import entities.Yahoo;
import enums.YahooEnum;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class WeeklyUpdateJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        Yahoo.getScoreAlerts(YahooEnum.WEEKLY_UPDATE);
    }
}
