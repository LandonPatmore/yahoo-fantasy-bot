package utils;

import services.Discord;
import services.GroupMe;
import services.Slack;

public class ServicesHandler {
    private static final Log log = new Log(ServicesHandler.class);
    private static boolean startupMessage = Postgres.getStartupMessageSent();

    private static final GroupMe groupMe = new GroupMe();
    private static final Discord discord = new Discord();
    private static final Slack slack = new Slack();

    private static boolean checkedEnvVariables = false;

    // TODO: Need someway to validate the services (env variables set up correctly, fallback, etc.)

    public static void sendMessage(String message) {
        groupMe.createMessage(message);
        discord.createMessage(message);
        slack.createMessage(message);
    }

    private static void checkEnvVariables() {
        if (!checkedEnvVariables) {
            log.debug("Checking environment variables.", false);

            if (System.getenv("GROUP_ME_ACCESS_TOKEN") == null) {
                groupMe.shouldNotUse();
            }

            if (System.getenv("GROUP_ME_BOT_ID") == null) {
                groupMe.shouldNotUse();
            }

            if (System.getenv("GROUP_ME_GROUP_ID") == null) {
                groupMe.shouldNotUse();
            }

            if (System.getenv("DISCORD_WEBHOOK_URL") == null) {
                discord.shouldNotUse();
            }

            if (System.getenv("SLACK_WEBHOOK_URL") == null) {
                discord.shouldNotUse();
            }

            logServicesUsed();

            checkedEnvVariables = true;
        }
    }

    public static void startupCheck() {
        checkEnvVariables();
        startupMessages();
    }

    /**
     * Startup messages that should be sent or not.
     */
    private static void startupMessages() {
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

    private static void logServicesUsed() {
        if (groupMe.shouldUse()) {
            log.debug("Using GroupMe.", false);
        }

        if (discord.shouldUse()) {
            log.debug("Using Discord.", false);
        }

        if (slack.shouldUse()) {
            log.debug("Using Slack.", false);
        }
    }
}
