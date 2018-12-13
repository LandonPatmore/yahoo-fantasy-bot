package jobs;

import com.mashape.unirest.http.Unirest;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class KeepAliveJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        Unirest.get("http://localhost:4567/keepalive");
    }
}
