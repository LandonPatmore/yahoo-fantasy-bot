package entities;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import enums.GroupMeEnum;
import helpers.GroupMeUser;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Log;

import java.io.InputStream;
import java.util.ArrayList;

public class GroupMe {
    private static final Log log = new Log(GroupMe.class);

    private static void createMessage(String message) {
        try {
            final HttpResponse<JsonNode> response = Unirest.post(GroupMeEnum.POST_URL.getValue())
                    .header("Content-Type", "application/json")
                    .body("{\"text\" : \"" + message + "\", \"bot_id\" : \"" + GroupMeEnum.BOT_ID.getValue() + "\"}")
                    .asJson();
            log.debug("Status Text: " + response.getStatusText() + " | Status: " + response.getStatus(), false);
        } catch (UnirestException e) {
            log.error(e.getLocalizedMessage(), true);
        }
    }

    public static ArrayList<GroupMeUser> getAllUsersInGroup() {
        final ArrayList<GroupMeUser> users = new ArrayList<>();

        try {
            final HttpResponse<JsonNode> response = Unirest.get(GroupMeEnum.GROUP_URL.getValue() + GroupMeEnum.GROUP_ID.getValue())
                    .queryString("token", GroupMeEnum.ACCESS_TOKEN.getValue())
                    .asJson();
            log.debug("Status Text: " + response.getStatusText() + " | Status: " + response.getStatus(), false);
            final JSONArray usersList = response.getBody().getObject().getJSONObject("response").getJSONArray("members");

            for (Object o : usersList) {
                if (o instanceof JSONObject) {
                    users.add(new GroupMeUser(((JSONObject) o).getInt("id"), ((JSONObject) o).getString("nickname")));
                }
            }

            return users;
        } catch (UnirestException e) {
            log.error(e.getLocalizedMessage(), true);
            return null;
        }
    }

    public static void sendMessage(String data) {
        if (data.length() < 1000) {
            createMessage(data);
        } else {
            createMessage(data.substring(0, 1001));
            sendMessage(data.substring(1001));
        }
    }

}
