package utils;

import com.github.scribejava.core.model.OAuth2AccessToken;
import entities.PostgresToken;

import java.sql.*;
import java.util.Date;

public class Postgres {
    private static final Log log = new Log(Postgres.class);

    private static Connection connection = null;

    private static Connection getConnection() {
        while (connection == null) {
            try {
                log.trace("Connection does not exist to database.  Creating...", false);

                final String url = System.getenv("JDBC_DATABASE_URL");
                connection = DriverManager.getConnection(url);

                log.debug("Connection established to database.", false);

                return connection;
            } catch (SQLException e) {
                log.error(e.getLocalizedMessage(), true);
            }
        }

        return connection;
    }

    public static void saveTokenData(OAuth2AccessToken token) {
        getConnection();

        try {
            log.trace("Attempting to save token data...", false);

            final String refreshToken = token.getRefreshToken();
            final String retrievedTime = Long.toString(new Date().getTime());
            final String rawResponse = token.getRawResponse();
            final String tokenType = token.getTokenType();
            final String accessToken = token.getAccessToken();
            final String expiresIn = token.getExpiresIn().toString();
            final String scope = token.getScope();

            final Statement statement = connection.createStatement();
            final String sql = "INSERT INTO tokens (\"yahooRefreshToken\",\"yahooTokenRetrievedTime\",\"yahooTokenRawResponse\",\"yahooTokenType\",\"yahooAccessToken\", \"yahooTokenExpireTime\", \"yahooTokenScope\")" + " VALUES (\'" + refreshToken + "\',\'" + retrievedTime + "\',\'" + rawResponse + "\',\'" + tokenType + "\',\'" + accessToken + "\',\'" + expiresIn + "\'," + scope + ")";

            statement.executeUpdate(sql);

            log.trace("Token data has been saved.", false);

            return;
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), false);
        }

    }

    public static void saveLastTimeChecked() {
        try {
            log.trace("Attempting to save token data...", false);

            final Statement statement = connection.createStatement();
            final String sql = "INSERT INTO latest_time (\"latest_time\")" + " VALUES (\'" + (new Date().getTime() / 1000) + "\')";

            statement.executeUpdate(sql);

            log.trace("Latest time has been saved.", false);
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), false);
        }
    }

    public static void markStartupMessageReceived() {
        try {
            log.trace("Marking startup message sent...", false);

            final Statement statement = connection.createStatement();
            final String sql = "INSERT INTO start_up_message_received (\"was_received\")" + " VALUES (\'" + true + "\')";

            statement.executeUpdate(sql);

            log.trace("Startup message marked sent.", false);
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), false);
        }
    }

    public static boolean getStartupMessageSent() {
        try {
            getConnection();

            final Statement statement = connection.createStatement();

            ResultSet row = statement.executeQuery("SELECT * FROM start_up_message_received ORDER BY \"was_received\" DESC LIMIT 1");

            if (row.next()) {
                return row.getBoolean("was_received");
            }

            return false;
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), false);
            return false;
        }
    }

    public static long getLatestTimeChecked() {
        try {
            getConnection();

            final Statement statement = connection.createStatement();

            ResultSet row = statement.executeQuery("SELECT * FROM latest_time ORDER BY \"latest_time\" DESC LIMIT 1");

            if (row.next()) {
                return row.getLong("latest_time");
            }

            return new Date().getTime() / 1000;
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), false);
            return new Date().getTime() / 1000;
        }
    }

    public static PostgresToken getLatestTokenData() {
        try {
            getConnection();

            final Statement statement = connection.createStatement();

            ResultSet row = statement.executeQuery("SELECT * FROM tokens ORDER BY \"yahooTokenRetrievedTime\" DESC LIMIT 1");

            if (row.next()) {
                final String refreshToken = row.getString("yahooRefreshToken");
                final long retrievedTime = row.getLong("yahooTokenRetrievedTime");
                final String rawResponse = row.getString("yahooTokenRawResponse");
                final String tokenType = row.getString("yahooTokenType");
                final String accessToken = row.getString("yahooAccessToken");
                final long expiresIn = row.getLong("yahooTokenExpireTime");
                final String scope = row.getString("yahooTokenScope");

                return new PostgresToken(new OAuth2AccessToken(accessToken, tokenType, (int) expiresIn, refreshToken, scope, rawResponse), retrievedTime);
            }
            return null;
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), false);
            return null;
        }
    }
}
