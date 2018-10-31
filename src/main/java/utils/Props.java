package utils;

import com.github.scribejava.core.model.OAuth2AccessToken;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

public class Props {
    private static final Log logger = new Log(Props.class);

    private static final Properties properties = new Properties();
    private static final InputStream stream = Props.class.getResourceAsStream("/settings.properties");

    /**
     * Loads the settings file
     *
     * @return if the file was loaded or not
     */
    public static Properties loadSettings() {
        try {
            properties.load(stream);
            if (properties.stringPropertyNames().size() == 0) {
                logger.info("Settings have not been loaded.", false);
                return null;
            }
            logger.info("Settings have been loaded.", false);
            return properties;
        } catch (IOException | NullPointerException e) {
            logger.error(e.getLocalizedMessage(), true);
            return null;
        }
    }

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

    public static Properties getProperties() {
        return properties;
    }

    public static String getYahooAccessToken() {
        return properties.getProperty("yahooAccessToken");
    }

    public static String getYahooRefreshToken() {
        return properties.getProperty("yahooRefreshToken");
    }

    public static int getYahooTokenExpireTime() {
        return Integer.parseInt(properties.getProperty("yahooTokenExpireTime"));
    }

    public static long getYahooTokenRetrievedTime() {
        return Long.parseLong(properties.getProperty("yahooTokenRetrievedTime"));
    }

    public static String getYahooTokenScope() {
        return properties.getProperty("yahooTokenScope").equals("") ? null : properties.getProperty("yahooTokenScope");
    }

    public static String getYahooTokenRawResponse() {
        return properties.getProperty("yahooTokenRawResponse");
    }

    public static String getYahooTokenType() {
        return properties.getProperty("yahooTokenType");
    }

    public static String getYahooClientId() {
        return properties.getProperty("yahooClientId");
    }

    public static String getYahooClientSecret() {
        return properties.getProperty("yahooClientSecret");
    }

    public static String getGroupMeBotId() {
        return properties.getProperty("groupMeBotId");
    }

    public static String getGroupMeGroupId() {
        return properties.getProperty("groupMeGroupId");
    }

    public static String getGroupMeAccessToken() {
        return properties.getProperty("groupMeAccessToken");
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
