package jobs;

import com.mashape.unirest.http.Unirest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import utils.JobRunner;

public class KeepAliveJob implements Job {
    private static final Logger log = LogManager.getLogger(JobRunner.class);

    @Override
    public void execute(JobExecutionContext context) {
        log.debug("Keeping server alive...");
        Unirest.get("http://localhost:4567/keepalive");
        log.debug("Server kept alive...");
    }
}
