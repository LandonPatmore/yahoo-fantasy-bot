package entities;

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

public class GroupMe {
    private static final Log log = new Log(GroupMe.class);

    private static final String POST_URL = "https://api.groupme.com/v3/bots/post";
    private static final String GROUP_URL = "https://api.groupme.com/v3/groups/";

    private static boolean startupMessage = Postgres.getStartupMessageSent();

    /**
     * Sends a message to the group.
     * @param message the message to send
     */
    private static void sendMessage(String message) {
        try {
            Thread.sleep(1000);
            if (message.endsWith("\\")) {
                message = message.substring(0, message.length() - 1);
            } else if (message.startsWith("n")) {
                message = message.substring(1);
            }
            log.debug(message, false);
            final HttpResponse<JsonNode> response = Unirest.post(POST_URL)
                    .header("Content-Type", "application/json")
                    .body("{\"text\" : \"" + message + "\", \"bot_id\" : \"" + GroupMeEnum.BOT_ID.getValue() + "\"}")
                    .asJson();
            log.debug("Status Text: " + response.getStatusText() + " | Status: " + response.getStatus(), false);
        } catch (UnirestException | InterruptedException e) {
            log.error(e.getLocalizedMessage(), true);
        }
    }

    /**
     * Gets all the users in the group.
     * @return list of users in group
     */
    public static ArrayList<GroupMeUser> getAllUsersInGroup() {
        final ArrayList<GroupMeUser> users = new ArrayList<>();

        try {
            final HttpResponse<JsonNode> response = Unirest.get(GROUP_URL + GroupMeEnum.GROUP_ID.getValue())
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

    /**
     * Startup messages that should be sent or not.
     */
    public static void startupMessages() {
        if (!startupMessage) {
            sendMessage("Hi there! It looks like this is the first time I am being started!  I can tell you about transactions that have happened, weekly matchup data, and score updates.  Thanks for using me!");
            startupMessage = true;
            Postgres.markStartupMessageReceived();

            log.trace("Startup message has been sent.", false);
        } else {
            if (System.getenv("RESTART_MESSAGE").equals("TRUE")) {
                sendMessage("Hi there! It looks like I was just restarted.  You may get data that is from earlier dates.  I am sorry about that.  I want to make sure you get all the data about your league!");

                log.trace("Restart message has been sent.", false);
            }
        }
    }

    /**
     * Creates a message.  Recursively creates messages when they are too large.
     * @param message the message to be sent
     */
    public static void createMessage(String message) {
        if (message.length() < 1000) {
            sendMessage(message);
        } else {
            sendMessage(message.substring(0, 1001));
            createMessage(message.substring(1001));
        }
    }

}
