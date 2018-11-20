package services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import shared.EnvHandler;
import shared.Log;

public class Slack extends Service {
    private static final Log log = new Log(Slack.class);

    public Slack() {
        super(EnvHandler.SLACK_WEBHOOK_URL.getValue());
    }

    @Override
    public void createMessage(String message) {
        if (shouldUse()) {
            final String correctedMessage = correctMessage(message);

            if (correctedMessage.length() < 40000) {
                sendMessage(correctedMessage);
            } else {
                sendMessage(correctedMessage.substring(0, 40001));
                createMessage(correctedMessage.substring(40001));
            }
        }
    }

    @Override
    void sendMessage(String message) {
        try {
            final HttpResponse<String> response = Unirest.post(url)
                    .header("Content-Type", "application/json")
                    .body("{\"text\" : \"" + message + "\"}")
                    .asString();
            log.debug("Status Text: " + response.getStatusText() + " | Status: " + response.getStatus(), false);
        } catch (UnirestException e) {
            log.error(e.getLocalizedMessage(), true);
        }
    }
}
