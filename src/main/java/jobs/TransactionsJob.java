package jobs;

import entities.Yahoo;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class TransactionsJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        Yahoo.parseTransactions();
    }
}
