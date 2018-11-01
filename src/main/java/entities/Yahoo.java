package entities;

import com.github.scribejava.apis.YahooApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import enums.YahooEnum;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import utils.Log;
import utils.Props;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Yahoo {
    private static final Log log = new Log(Yahoo.class);
    private static final OAuth20Service service = new ServiceBuilder(YahooEnum.CLIENT_ID.getValue())
            .apiSecret(YahooEnum.CLIENT_SECRET.getValue())
            .callback(OAuthConstants.OOB)
            .build(YahooApi20.instance());
    private static OAuth2AccessToken currentToken;

    private static boolean isTokenExpired(long current, Long retrieved, Integer expiresIn) {
        final long timeElapsed = ((current - retrieved) / 1000);
        final boolean hasExpired = timeElapsed > expiresIn;
        log.debug("Token expired: " + hasExpired + " | Seconds remaining until expiration: " + (expiresIn - timeElapsed), false);
        return hasExpired;
    }

    private static void refreshExpiredToken() {
        try {
            log.trace("Refreshing token...", false);
            currentToken = service.refreshAccessToken(currentToken.getRefreshToken());
            log.trace("Token refreshed.", false);
            saveAuthenticationData();
        } catch (IOException | InterruptedException | ExecutionException e) {
            log.error(e.getLocalizedMessage(), true);
        }
    }

    private static void saveAuthenticationData() {
        Props.setYahooTokenData(currentToken);
    }

    private static void initialAuthentication() {
        try {
            final Scanner scanner = new Scanner(System.in);

            log.trace("Initially authorizing...", false);

            // Obtain the Request Token
            log.trace("Fetching request token...", false);
            log.trace("Got request token.", false);

            log.trace("Authorize usage here:", false);
            log.trace(service.getAuthorizationUrl(), false);
            log.trace("Paste the verification string:", false);
            final String oauthVerifier = scanner.nextLine();

            // Trade the Request Token and Verfier for the Access Token
            log.trace("Trading request token for access token...", false);
            currentToken = service.getAccessToken(oauthVerifier);
            saveAuthenticationData();
            log.trace("Access token received.  Authorized successfully.", false);
        } catch (InterruptedException | ExecutionException |
                IOException e) {
            log.error("Access token not received.", false);
            log.error(e.getLocalizedMessage(), true);
        }
    }

    private static void tradeTransaction(String entityOne, String entityTwo, String time, Elements players) {
        log.trace("Trade Transaction has occurred after last check...notifying.", false);

        final Transaction t = new TradeTransaction(entityOne, entityTwo, time);
        for (Element player : players) {
            final String playerName = player.select("full").first().text() + " (" + player.select("editorial_team_abbr").first().text() + ", " + player.select("display_position").first().text() + ")";
            final String playerAssociatedWith = player.select("source_team_name").text();

            t.addPlayerToEntity(playerName, playerAssociatedWith);
        }
    }

    private static void addTransaction(String entityOne, String entityTwo, String time, Elements players) {
        log.trace("Add Transaction has occurred after last check...notifying.", false);

        final Transaction t = new AddTransaction(entityOne, entityTwo, time);
        for (Element player : players) {
            final String playerName = player.select("full").first().text() + " (" + player.select("editorial_team_abbr").first().text() + ", " + player.select("display_position").first().text() + ")";
            final String playerAssociatedWith = player.select("source_type").first().text();

            t.addPlayerToEntity(playerName, playerAssociatedWith);
        }
    }

    private static void dropTransaction(String entityOne, String entityTwo, String time, Elements players) {
        log.trace("Drop Transaction has occurred after last check...notifying.", false);

        final Transaction t = new DropTransaction(entityOne, entityTwo, time);
        for (Element player : players) {
            final String playerName = player.select("full").first().text() + " (" + player.select("editorial_team_abbr").first().text() + ", " + player.select("display_position").first().text() + ")";
            final String playerAssociatedWith = player.select("source_team_name").text();

            t.addPlayerToEntity(playerName, playerAssociatedWith);
        }
    }

    private static void parseTransaction(Document doc) {
        final Elements elements = doc.select("transaction");

        for (Element trans : elements) {
            final String type = trans.select("type").first().text();
            final String time = trans.select("timestamp").first().text();
            if (Long.parseLong(time) > Long.parseLong(Props.getLastCheckedTransactions())) {
            switch (type) {
                case "add": {
                    addTransaction(trans.select("destination_team_name").text(), trans.select("source_type").text(), time, trans.select("player"));
                    break;
                }
                case "drop": {
                    dropTransaction(trans.select("source_team_name").text(), trans.select("destination_type").text(), time, trans.select("player"));
                    break;
                }
                case "add/drop": {
                    log.trace("Add/Drop Transaction has occurred after last check...notifying.", false);

                    final ArrayList<String> addedPlayers = new ArrayList<>();
                    final ArrayList<String> droppedPlayers = new ArrayList<>();

                    final AddDropTransaction t = new AddDropTransaction(time);
                    for (Element player : trans.select("player")) {
                        if (player.select("type").first().text().equals("add")) {
                            addedPlayers.add(player.select("full").first().text() + " (" + player.select("editorial_team_abbr").first().text() + ", " + player.select("display_position").first().text() + ")");
                            t.addTransaction(player.select("destination_team_name").text(), player.select("source_type").text(), addedPlayers); // TODO: Terrible fix this
                        } else {
                            droppedPlayers.add(player.select("full").first().text() + " (" + player.select("editorial_team_abbr").first().text() + ", " + player.select("display_position").first().text() + ")");
                            t.dropTransaction(player.select("source_team_name").text(), player.select("destination_type").text(), droppedPlayers);
                        }
                    }
                    System.out.println(t.getTransactionString());
                    break;
                }
                case "trade": {
                    tradeTransaction(trans.select("trader_team_name").text(), trans.select("tradee_team_name").text(), time, trans.select("player"));
                    break;
                }
            }
        } else{
            log.trace("Transactions past this date are older than last checked time.  Not checking anymore.", false);
            break;
        }
        }
    }

    public static void authenticate() {
        if (Props.doesOAuthTokenDataExist()) {
            final String accessToken = Props.getYahooAccessToken();
            final String refreshToken = Props.getYahooRefreshToken();
            final String tokenType = Props.getYahooTokenType();
            final String scope = Props.getYahooTokenScope();
            final String rawResponse = Props.getYahooTokenRawResponse();
            final Integer expireTime = Props.getYahooTokenExpireTime();

            currentToken = new OAuth2AccessToken(accessToken, tokenType, expireTime, refreshToken, scope, rawResponse);

            final long currentTime = System.currentTimeMillis();
            final Long dateRetrieved = Props.getYahooTokenRetrievedTime();

            if (isTokenExpired(currentTime, dateRetrieved, expireTime)) {
                refreshExpiredToken();
            }
        } else {
            initialAuthentication();
        }
    }

    public static void grabData(String URL) {
        try {
            log.debug("Grabbing Data...", false);
            final OAuthRequest request = new OAuthRequest(Verb.GET, URL);
            service.signRequest(currentToken, request);
            final Response response = service.execute(request);
            log.debug("Data grabbed.", false);

            final Document doc = Jsoup.parse(response.getBody(), "", Parser.xmlParser());
            System.out.println(doc);
//            parseTransaction(doc);

            Props.setLastCheckedTransactions();
        } catch (IOException | InterruptedException | ExecutionException e) {
            log.error(e.getLocalizedMessage(), false);
        }
    }

}
