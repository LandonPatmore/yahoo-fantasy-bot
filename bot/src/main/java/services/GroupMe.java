package services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shared.EnvHandler;

public class GroupMe extends Service {
    private static final Logger log = LogManager.getLogger(GroupMe.class);

    public GroupMe() {
        super("https://api.groupme.com/v3/bots/post");
    }

    @Override
    public void createMessage(String message) {
        if(shouldUse()) {
            final String correctedMessage = correctMessage(message);

            if (correctedMessage.length() < 1000) {
                sendMessage(correctedMessage);
            } else {
                sendMessage(correctedMessage.substring(0, 1000));
                createMessage(correctedMessage.substring(1000));
            }
        }
    }

    @Override
    void sendMessage(String message) {
        try {
            final HttpResponse<JsonNode> response = Unirest.post(url)
                    .header("Content-Type", "application/json")
                    .body("{\"text\" : \"" + message + "\", \"bot_id\" : \"" + EnvHandler.GROUP_ME_BOT_ID.getValue() + "\"}")
                    .asJson();
            log.debug("Status Text: " + response.getStatusText() + " | Status: " + response.getStatus());
        } catch (UnirestException e) {
            log.error(e.getLocalizedMessage(), new Throwable());
        }
    }

}
