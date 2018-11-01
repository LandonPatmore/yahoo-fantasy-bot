package utils;

import com.github.scribejava.core.model.OAuth2AccessToken;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

public class Props {
    private static final Log logger = new Log(Props.class);

    private static final Properties properties = new Properties();
    private static final InputStream stream = Props.class.getResourceAsStream("/settings.properties");

    /**
     * Saves the settings to the settings file
     *
     * @return if the file was saved or not
     */
    private static boolean saveSettings() {
        try {
            properties.store(new FileWriter(Props.class.getResource("/settings.properties").getPath()), null);
            logger.info("Settings have been saved.", false);
            return true;
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), true);
            // TODO: Have properties set to default and alert user
            return false;
        }
    }

    /**
     * Loads the settings file
     *
     * @return if the file was loaded or not
     */
    public static void loadSettings() {
        try {
            properties.load(stream);
            logger.info("Settings have been loaded.", false);
        } catch (IOException | NullPointerException e) {
            logger.error(e.getLocalizedMessage(), true);
        }
    }

    public static Properties getProperties() {
        return properties;
    }

    public static boolean doesDataHaveIntegrity() {
        return Stream.of(getYahooClientId(), getYahooClientSecret(),
                getGroupMeAccessToken(), getGroupMeBotId(),
                getGroupMeGroupId(), getYahooLeagueId(), getLastCheckedTransactions()).allMatch(Objects::nonNull);
    }

    public static String getYahooClientId() {
        return properties.getProperty("yahooClientId");
    }

    public static String getYahooClientSecret() {
        return properties.getProperty("yahooClientSecret");
    }

    public static String getGroupMeAccessToken() {
        return properties.getProperty("groupMeAccessToken");
    }

    public static String getGroupMeBotId() {
        return properties.getProperty("groupMeBotId");
    }

    public static String getGroupMeGroupId() {
        return properties.getProperty("groupMeGroupId");
    }

    public static boolean doesOAuthTokenDataExist() {
        return Stream.of(getYahooAccessToken(), getYahooRefreshToken(), getYahooTokenRawResponse(),
                getYahooTokenScope(), getYahooTokenType(), getYahooTokenExpireTime(),
                getYahooTokenRetrievedTime()).allMatch(Objects::nonNull);
    }

    public static String getYahooAccessToken() {
        return properties.getProperty("yahooAccessToken");
    }

    public static String getYahooRefreshToken() {
        return properties.getProperty("yahooRefreshToken");
    }

    public static String getYahooTokenRawResponse() {
        return properties.getProperty("yahooTokenRawResponse");
    }

    public static String getYahooTokenScope() {
        return properties.getProperty("yahooTokenScope");
    }

    public static String getYahooTokenType() {
        return properties.getProperty("yahooTokenType");
    }

    public static String getYahooLeagueId() {
        return properties.getProperty("yahooLeagueId");
    }

    public static Integer getYahooTokenExpireTime() {
        final String prop = properties.getProperty("yahooTokenExpireTime");
        return prop == null ? null : Integer.parseInt(prop);
    }

    public static Long getYahooTokenRetrievedTime() {
        final String prop = properties.getProperty("yahooTokenRetrievedTime");
        return prop == null ? null : Long.parseLong(prop);
    }

    public static void setLastCheckedTransactions() {
        properties.setProperty("lastCheckedTransactions", Long.toString(new Date().getTime() / 1000));

        saveSettings();
    }

    public static String getLastCheckedTransactions() {
        return properties.getProperty("lastCheckedTransactions");
    }

    public static void setYahooTokenData(OAuth2AccessToken token) {
        properties.setProperty("yahooAccessToken", token.getAccessToken());
        properties.setProperty("yahooRefreshToken", token.getRefreshToken());
        properties.setProperty("yahooTokenExpireTime", token.getExpiresIn().toString());
        properties.setProperty("yahooTokenRetrievedTime", Long.toString(new Date().getTime()));
        properties.setProperty("yahooTokenScope", token.getScope() == null ? "" : token.getScope());
        properties.setProperty("yahooTokenRawResponse", token.getRawResponse());
        properties.setProperty("yahooTokenType", token.getTokenType());

        saveSettings();
    }


}
