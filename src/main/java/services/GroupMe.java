package services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import enums.GroupMeEnum;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Log;
import utils.Postgres;

import java.util.ArrayList;

public class GroupMe extends Service {
    private static final Log log = new Log(GroupMe.class);

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
                    .body("{\"text\" : \"" + message + "\", \"bot_id\" : \"" + GroupMeEnum.BOT_ID.getValue() + "\"}")
                    .asJson();
            log.debug("Status Text: " + response.getStatusText() + " | Status: " + response.getStatus(), false);
        } catch (UnirestException e) {
            log.error(e.getLocalizedMessage(), true);
        }
    }

}
