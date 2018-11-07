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
import utils.Postgres;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Yahoo {
    private static final Log log = new Log(Yahoo.class);

    private static final String BASE_URL = "https://fantasysports.yahooapis.com/fantasy/v2/";
    private static final String LEAGUE_KEY = "nfl.l." + System.getenv("YAHOO_LEAGUE_ID");

    private static final OAuth20Service service = new ServiceBuilder(YahooEnum.CLIENT_ID.getValue())
            .apiSecret(YahooEnum.CLIENT_SECRET.getValue())
            .callback(OAuthConstants.OOB)
            .build(YahooApi20.instance());
    private static OAuth2AccessToken currentToken;
    private static Long lastCheckedTransactions = new Date().getTime() / 1000; // Get the current time once application starts

    private static boolean isTokenExpired(long current, Long retrieved, int expiresIn) {
        final long timeElapsed = ((current - retrieved) / 1000);
        final boolean hasExpired = timeElapsed >= expiresIn;
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

    private static void initialAuthentication() {
        while (true) {
            try {
                final Scanner scanner = new Scanner(System.in);

                log.trace("Initial authorization...", false);

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
                return;
            } catch (InterruptedException | ExecutionException |
                    IOException e) {
                log.error("Could not authenticate with Yahoo's servers.", false);
                log.error(e.getLocalizedMessage(), true);
            }
        }
    }

    private static Transaction tradeTransaction(String entityOne, String entityTwo, String time, Elements players) {
        log.trace("Trade Transaction has occurred after last check...notifying.", false);

        final Transaction t = new TradeTransaction(entityOne, entityTwo, time);
        for (Element player : players) {
            final String playerName = player.select("full").first().text() + " (" + player.select("editorial_team_abbr").first().text() + ", " + player.select("display_position").first().text() + ")";
            final String playerAssociatedWith = player.select("source_team_name").text();

            t.addPlayerToEntity(playerName, playerAssociatedWith);
        }

        return t;
    }

    private static Transaction addTransaction(String entityOne, String entityTwo, String time, Elements players) {
        log.trace("Add Transaction has occurred after last check...notifying.", false);

        final Transaction t = new AddTransaction(entityOne, entityTwo, time);
        for (Element player : players) {
            final String playerName = player.select("full").first().text() + " (" + player.select("editorial_team_abbr").first().text() + ", " + player.select("display_position").first().text() + ")";
            final String playerAssociatedWith = player.select("source_type").first().text();

            t.addPlayerToEntity(playerName, playerAssociatedWith);
        }

        return t;
    }

    private static Transaction dropTransaction(String entityOne, String entityTwo, String time, Elements players) {
        log.trace("Drop Transaction has occurred after last check...notifying.", false);

        final Transaction t = new DropTransaction(entityOne, entityTwo, time);
        for (Element player : players) {
            final String playerName = player.select("full").first().text() + " (" + player.select("editorial_team_abbr").first().text() + ", " + player.select("display_position").first().text() + ")";
            final String playerAssociatedWith = player.select("source_team_name").text();

            t.addPlayerToEntity(playerName, playerAssociatedWith);
        }

        return t;
    }

    private static Transaction addDropTransaction(String time, Elements players) {
        log.trace("Add/Drop Transaction has occurred after last check...notifying.", false);

        final AddDropTransaction t = new AddDropTransaction(time);
        for (Element player : players) {
            final String playerName = player.select("full").first().text() + " (" + player.select("editorial_team_abbr").first().text() + ", " + player.select("display_position").first().text() + ")";
            final String playerAssociatedWith = player.select("source_team_name").text();

            if (player.select("type").text().equals("add")) {
                final AddTransaction addTransaction = new AddTransaction(player.select("destination_team_name").text(), player.select("source_type").text(), time);

                addTransaction.addPlayerToEntity(playerName, playerAssociatedWith);
                t.addTransaction(addTransaction);
            } else {
                final DropTransaction dropTransaction = new DropTransaction(player.select("source_team_name").text(), player.select("destination_type").text(), time);

                dropTransaction.addPlayerToEntity(playerName, playerAssociatedWith);
                t.addTransaction(dropTransaction);
            }
        }

        return t;
    }

    private static Transaction commishTransaction(String time) {
        return new CommishTransaction(time);
    }

    public static void parseTransactions() {
        final Document transactionsData = grabData(BASE_URL + "league/" + LEAGUE_KEY + "/transactions");

        if (transactionsData != null) {
            final ArrayList<Transaction> transactions = new ArrayList<>();

            final Elements elements = transactionsData.select("transaction");

            for (Element trans : elements) {
                final String type = trans.select("type").first().text();
                final String time = trans.select("timestamp").first().text();
                if (Long.parseLong(time) >= lastCheckedTransactions) {
                    final Elements players = trans.select("player");
                    switch (type) {
                        case "add": {
                            transactions.add(addTransaction(trans.select("destination_team_name").text(), trans.select("source_type").text(), time, players));
                            break;
                        }
                        case "drop": {
                            transactions.add(dropTransaction(trans.select("source_team_name").text(), trans.select("destination_type").text(), time, players));
                            break;
                        }
                        case "add/drop": {
                            transactions.add(addDropTransaction(time, players));
                            break;
                        }
                        case "trade": {
                            transactions.add(tradeTransaction(trans.select("trader_team_name").text(), trans.select("tradee_team_name").text(), time, players));
                            break;
                        }
                        case "commish": {
                            transactions.add(commishTransaction(time));
                        }
                    }
                } else {
                    log.trace("Transactions past this date are older than last checked time.  Not checking anymore.", false);
                    break;
                }
            }
            lastCheckedTransactions = new Date().getTime() / 1000;

            buildGroupMeMessage(transactions);
        } else {
            log.debug("Transaction Data was null.", true);
        }
    }

    private static void buildGroupMeMessage(ArrayList<Transaction> transactions) {
        if (transactions.size() != 0) {
            log.debug("Building out GroupMe message.", false);

            Collections.sort(transactions);

            final StringBuilder builder = new StringBuilder();
            for (Transaction t : transactions) {
                builder.append(t.getTransactionString());
            }

            GroupMe.sendMessage(builder.toString());

            log.debug("GroupMe message sent.", false);
        } else {
            log.debug("There are no transactions.  No message has been sent.", false);
        }
    }

    private static void saveAuthenticationData() {
        Postgres.saveTokenData(currentToken);
    }

    private static HashMap<String, YahooTeam> getStandings(Document doc) {
        if (doc != null) {
            final HashMap<String, YahooTeam> standings = new HashMap<>();
            final Elements teams = doc.select("team");

            for (Element t : teams) {
                final String teamName = t.select("name").text();
                final String rank = t.select("rank").text();
                final String wins = t.select("wins").first().text();
                final String losses = t.select("losses").first().text();
                final String ties = t.select("ties").first().text();
                final String streak = t.select("streak").select("value").text() + t.select("streak").select("type").text().substring(0, 1).toUpperCase();
                final String pointsFor = t.select("points_for").text();
                final String pointsAgainst = t.select("points_against").text();

                final YahooTeam team = new YahooTeam(rank, wins, losses, ties, streak, pointsFor, pointsAgainst);
                standings.put(teamName, team);
            }

            return standings;
        } else {
            log.error("Doc was null.", false);
        }

        return null;
    }

    private static ArrayList<Matchup> getMatchups(Document doc, HashMap<String, YahooTeam> standings) {
        if (doc != null) {
            final Elements matchups = doc.select("matchup");
            final ArrayList<Matchup> matches = new ArrayList<>();

            for (Element m : matchups) {
                for (Element te : m.select("teams")) {
                    final Elements teams = te.select("team");
                    final String teamOne = teams.get(0).select("name").text();
                    final String teamTwo = teams.get(1).select("name").text();
                    final Matchup matchup = new Matchup(teamOne, teamTwo, standings.get(teamOne), standings.get(teamTwo));

                    for (Element t : teams) {
                        final String winProbability = t.select("win_probability").text();
                        final String projectedPoints = t.select("team_projected_points").select("total").text();
                        final String currentScore = t.select("team_points").select("total").text();
                        final String team = t.select("name").text();

                        matchup.addMatchUpData(winProbability, projectedPoints, currentScore, team);
                    }

                    matches.add(matchup);
                }
            }

            return matches;
        } else {
            log.error("Doc was null", true);
        }

        return null;
    }

    public static void scoreAlert(YahooEnum type) {
        final Document standingsData = grabData(BASE_URL + "league/" + LEAGUE_KEY + "/standings");
        final HashMap<String, YahooTeam> standings = getStandings(standingsData);
        final Document scoreboardData = grabData(BASE_URL + "league/" + LEAGUE_KEY + "/scoreboard");
        final ArrayList<Matchup> matchups = getMatchups(scoreboardData, standings);

        if (matchups != null) {
            final StringBuilder message = new StringBuilder();
            if (type.equals(YahooEnum.WEEKLY_UPDATE)) {
                message.append("===WEEKLY MATCHUPS===\\n\\n");
                for (Matchup mat : matchups) {
                    message.append(mat.weeklyMatchupAlert()).append("\\n\\n============\\n\\n");
                }
            } else if (type.equals(YahooEnum.SCORE_UPDATE)) {
                message.append("===SCORE UPDATE===\\n\\n");
                for (Matchup mat : matchups) {
                    message.append(mat.scoreboardAlert()).append("\\n\\n");
                }
            } else if (type.equals(YahooEnum.CLOSE_SCORE_UPDATE)) {
                final ArrayList<String> closeScores = new ArrayList<>();

                message.append("===CLOSE SCORE UPDATE===\\n\\n");
                for (Matchup mat : matchups) {
                    if (mat.isScoreClose()) {
                        closeScores.add(mat.scoreboardAlert());
                    }
                }

                if (closeScores.size() != 0) {
                    for (String s : closeScores) {
                        message.append(s).append("\\n\\n");
                    }
                } else {
                    log.debug("There were no close scores.  Not sending messages.", false);
                    return;
                }
            }

            GroupMe.sendMessage(message.toString());
        } else {
            log.debug("Matchups were null.  Not sending message.", false);
        }
    }

    private static void authenticate() {
        final PostgresToken token = Postgres.getLatestTokenData();
        if (token != null) {
            currentToken = token.getToken();

            final long currentTime = System.currentTimeMillis();
            final Long dateRetrieved = token.getRetrievedTime();

            if (isTokenExpired(currentTime, dateRetrieved, currentToken.getExpiresIn())) {
                refreshExpiredToken();
            }
        } else {
            initialAuthentication();
        }
    }

    private static Document grabData(String URL) {
        try {
            authenticate();
            log.debug("Grabbing Data...", false);
            final OAuthRequest request = new OAuthRequest(Verb.GET, URL);
            service.signRequest(currentToken, request);
            final Response response = service.execute(request);
            log.debug("Data grabbed.", false);

            return Jsoup.parse(response.getBody(), "", Parser.xmlParser());
        } catch (IOException | InterruptedException | ExecutionException e) {
            log.error(e.getLocalizedMessage(), false);
            return null;
        }
    }

}
