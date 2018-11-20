package utils;

import services.Discord;
import services.GroupMe;
import services.Service;
import services.Slack;
import shared.EnvHandler;
import shared.Log;
import shared.Postgres;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServicesHandler {
    private static final Log log = new Log(ServicesHandler.class);
    private static boolean startupMessage = Postgres.getStartupMessageSent();

    private static final GroupMe groupMe = new GroupMe();
    private static final Discord discord = new Discord();
    private static final Slack slack = new Slack();

    private static final Service[] services = {groupMe, discord, slack};

    private static boolean checkedEnvVariables = false;

    // TODO: Need someway to validate the services (env variables set up correctly, fallback, etc.)

    public static void sendMessage(String message) {
        final ExecutorService executorService = Executors.newFixedThreadPool(services.length);
        for (Service s : services) {
            s.setCurrentMessage(message);
            executorService.submit(s);
        }
        executorService.shutdown();
    }

    private static void checkEnvVariables() {
        if (!checkedEnvVariables) {
            log.debug("Checking environment variables.", false);

            TimeZoneData.checkTimezoneEnv();

            if (EnvHandler.GROUP_ME_ACCESS_TOKEN.getValue() == null) {
                groupMe.shouldNotUse();
            }

            if (EnvHandler.GROUP_ME_BOT_ID.getValue() == null) {
                groupMe.shouldNotUse();
            }

            if (EnvHandler.GROUP_ME_GROUP_ID.getValue() == null) {
                groupMe.shouldNotUse();
            }

            if (EnvHandler.DISCORD_WEBHOOK_URL.getValue() == null) {
                discord.shouldNotUse();
            }

            if (EnvHandler.SLACK_WEBHOOK_URL.getValue() == null) {
                slack.shouldNotUse();
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
            // TODO: Show the current settings that the bot is using
            startupMessage = true;
            Postgres.markStartupMessageReceived();

            log.trace("Startup message has been sent.", false);
        } else {
            if (("TRUE").equalsIgnoreCase(EnvHandler.RESTART_MESSAGE.getValue())) {
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
