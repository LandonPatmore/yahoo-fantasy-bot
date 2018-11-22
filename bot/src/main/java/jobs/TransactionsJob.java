package jobs;

import utils.ServicesHandler;
import utils.Yahoo;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class TransactionsJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        final String alert = Yahoo.getTransactions();
        if(alert != null) {
            ServicesHandler.sendMessage(alert);
        }
    }
}
