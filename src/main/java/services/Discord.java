package services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import utils.Log;

public class Discord extends Service {
    private static final Log log = new Log(Discord.class);

    public Discord() {
        super(System.getenv("DISCORD_WEBHOOK_URL"));
    }

    @Override
    public void createMessage(String message) {
        if (shouldUse()) {
            final String correctedMessage = correctMessage(message);

            if (correctedMessage.length() < 2000) {
                sendMessage(correctedMessage);
            } else {
                sendMessage(correctedMessage.substring(0, 2001));
                createMessage(correctedMessage.substring(2001));
            }
        }
    }

    @Override
    void sendMessage(String message) {
        try {
            final HttpResponse<JsonNode> response = Unirest.post(url)
                    .header("Content-Type", "application/json")
                    .body("{\"content\" : \"" + message + "\"}")
                    .asJson();
            log.debug("Status Text: " + response.getStatusText() + " | Status: " + response.getStatus(), false);
        } catch (UnirestException e) {
            log.error(e.getLocalizedMessage(), true);
        }
    }
}
