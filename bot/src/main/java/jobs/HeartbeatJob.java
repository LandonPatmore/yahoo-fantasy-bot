package jobs;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import utils.EnvHandler;
import utils.JobRunner;

public class HeartbeatJob implements Job {
    private static final Logger log = LogManager.getLogger(JobRunner.class);

    @Override
    public void execute(JobExecutionContext context) {
        log.debug("Keeping server alive...");
        try {
            HttpResponse<String> response = Unirest.get("https://" + EnvHandler.APP_NAME.getValue() + ".herokuapp.com/keepalive")
                    .asString();
            if(Boolean.valueOf(response.getBody())){
                log.debug("Server kept alive...");
            } else {
                log.error("Server could not be kept alive.");
                log.error(response.getStatus() + ": " + response.getStatusText());
            }
        } catch (UnirestException e) {
            log.error("Server could not be kept alive.");
            log.error(e.getLocalizedMessage(), new Throwable());
        }
    }
}
