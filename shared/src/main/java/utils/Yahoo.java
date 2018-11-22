package utils;

import com.github.scribejava.apis.YahooApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import entities.Matchup;
import entities.YahooTeam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import transactions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class Yahoo {
    private static final Logger log = LogManager.getLogger(Yahoo.class);

    private static final String BASE_URL = "https://fantasysports.yahooapis.com/fantasy/v2/";
    private static final String LEAGUE_KEY = "nfl.l." + EnvHandler.YAHOO_LEAGUE_ID.getValue();

    private static OAuth20Service service = new ServiceBuilder(EnvHandler.YAHOO_CLIENT_ID.getValue())
            .apiSecret(EnvHandler.YAHOO_CLIENT_SECRET.getValue())
            .callback(OAuthConstants.OOB)
            .build(YahooApi20.instance());
    private static OAuth2AccessToken currentToken;

    /**
     * Checks to see whether or not a token is expired
     *
     * @param retrieved when the token was initially retrieved
     * @param expiresIn when the token expires
     * @return if the token is expired
     */
    private static boolean isTokenExpired(Long retrieved, int expiresIn) {
        final long timeElapsed = ((System.currentTimeMillis() - retrieved) / 1000);
        final boolean hasExpired = timeElapsed >= expiresIn;
        log.debug("Token expired: " + hasExpired + " | Seconds remaining until expiration: " + (expiresIn - timeElapsed));
        return hasExpired;
    }

    /**
     * Refreshes an expired token and then saves it to the DB.
     */
    private static void refreshExpiredToken() {
        try {
            log.trace("Refreshing token...");
            currentToken = service.refreshAccessToken(currentToken.getRefreshToken());
            log.trace("Token refreshed.");
            saveAuthenticationData();
        } catch (IOException | InterruptedException | ExecutionException e) {
            log.error(e.getLocalizedMessage(), new Throwable());
        }
    }

    public static OAuth20Service getService() {
        return service;
    }

    /**
     * Creates a TradeTransaction
     *
     * @param entityOne first entity
     * @param entityTwo second entity
     * @param time      the time the transaction happened
     * @param players   list of players data
     * @return TradeTransaction
     */
    private static Transaction tradeTransaction(String entityOne, String entityTwo, String time, Elements players, String status) {
        log.trace("Trade Transaction has occurred after last check...notifying.");

        final Transaction t = new TradeTransaction(entityOne, entityTwo, time, status);
        for (Element player : players) {
            final String playerName = player.select("full").first().text() + " (" + player.select("editorial_team_abbr").first().text() + ", " + player.select("display_position").first().text() + ")";
            final String playerAssociatedWith = player.select("source_team_name").text();

            t.addPlayerToEntity(playerName, playerAssociatedWith);
        }

        return t;
    }

    /**
     * Creates an AddTransaction
     *
     * @param entityOne first entity
     * @param entityTwo second entity
     * @param time      the time the transaction happened
     * @param players   list of players data
     * @return AddTransaction
     */
    private static Transaction addTransaction(String entityOne, String entityTwo, String time, Elements players) {
        log.trace("Add Transaction has occurred after last check...notifying.");

        final Transaction t = new AddTransaction(entityOne, entityTwo, time);
        for (Element player : players) {
            final String playerName = player.select("full").first().text() + " (" + player.select("editorial_team_abbr").first().text() + ", " + player.select("display_position").first().text() + ")";
            final String playerAssociatedWith = player.select("source_type").first().text();

            t.addPlayerToEntity(playerName, playerAssociatedWith);
        }

        return t;
    }

    /**
     * Creates a DropTransaction
     *
     * @param entityOne first entity
     * @param entityTwo second entity
     * @param time      the time the transaction happened
     * @param players   list of players data
     * @return DropTransaction
     */
    private static Transaction dropTransaction(String entityOne, String entityTwo, String time, Elements players) {
        log.trace("Drop Transaction has occurred after last check...notifying.");

        final Transaction t = new DropTransaction(entityOne, entityTwo, time);
        for (Element player : players) {
            final String playerName = player.select("full").first().text() + " (" + player.select("editorial_team_abbr").first().text() + ", " + player.select("display_position").first().text() + ")";
            final String playerAssociatedWith = player.select("source_team_name").text();

            t.addPlayerToEntity(playerName, playerAssociatedWith);
        }

        return t;
    }

    /**
     * Creates an AddDropTransaction
     *
     * @param time    the time the transaction happened
     * @param players list of players data
     * @return AddDropTransaction
     */
    private static Transaction addDropTransaction(String time, Elements players) {
        if ("TRUE".equalsIgnoreCase(EnvHandler.SHOW_DROP_ALERT.getValue())) {
            log.trace("Add/Drop Transaction has occurred after last check...notifying.");
        } else {
            log.trace("Add/Drop Transaction has occurred after last check, only showing Add alert because of settings...notifying.");
        }

        final AddDropTransaction t = new AddDropTransaction(time);
        for (Element player : players) {
            final String playerName = player.select("full").first().text() + " (" + player.select("editorial_team_abbr").first().text() + ", " + player.select("display_position").first().text() + ")";
            final String playerAssociatedWith = player.select("source_team_name").text();

            if ("TRUE".equalsIgnoreCase(EnvHandler.SHOW_DROP_ALERT.getValue())) {
                if (player.select("type").text().equals("add")) {
                    final AddTransaction addTransaction = new AddTransaction(player.select("destination_team_name").text(), player.select("source_type").text(), time);

                    addTransaction.addPlayerToEntity(playerName, playerAssociatedWith);
                    t.addTransaction(addTransaction);
                } else {
                    final DropTransaction dropTransaction = new DropTransaction(player.select("source_team_name").text(), player.select("destination_type").text(), time);

                    dropTransaction.addPlayerToEntity(playerName, playerAssociatedWith);
                    t.addTransaction(dropTransaction);
                }
            } else {
                if (player.select("type").text().equals("add")) {
                    final AddTransaction addTransaction = new AddTransaction(player.select("destination_team_name").text(), player.select("source_type").text(), time);
                    addTransaction.addPlayerToEntity(playerName, playerAssociatedWith);
                    return addTransaction;
                }
            }
        }

        return t;
    }

    /**
     * Creates a CommishTransaction.
     *
     * @param time the time the transaction happened
     * @return CommishTransaction
     */
    private static Transaction commishTransaction(String time) {
        return new CommishTransaction(time);
    }

    /**
     * Parses transactions data.
     */
    public static String parseTransactions() {
        final Document transactionsData = grabData(BASE_URL + "league/" + LEAGUE_KEY + "/transactions");

        if (transactionsData != null) {
            final ArrayList<Transaction> transactions = new ArrayList<>();

            final Elements elements = transactionsData.select("transaction");

            for (Element trans : elements) {
                final String type = trans.select("type").first().text();
                final String time = trans.select("timestamp").first().text();

                if (Long.parseLong(time) >= Postgres.getLatestTimeChecked()) {
                    final Elements players = trans.select("player");
                    switch (type) {
                        case "add":
                            transactions.add(addTransaction(trans.select("destination_team_name").text(), trans.select("source_type").text(), time, players));
                            break;
                        case "drop":
                            if ("TRUE".equalsIgnoreCase(EnvHandler.SHOW_DROP_ALERT.getValue())) {
                                transactions.add(dropTransaction(trans.select("source_team_name").text(), trans.select("destination_type").text(), time, players));
                            }
                            break;
                        case "add/drop":
                            transactions.add(addDropTransaction(time, players));
                            break;
                        case "trade":
                            transactions.add(tradeTransaction(trans.select("trader_team_name").text(), trans.select("tradee_team_name").text(), time, players, trans.select("status").text()));
                            break;
                        case "commish":
                            transactions.add(commishTransaction(time));
                            break;
                        default:
                            break;
                    }
                } else {
                    log.trace("Transactions past this date are older than last checked time.  Not checking anymore.");
                    break;
                }
            }

            Postgres.saveLastTimeChecked();

            return buildTransactionsString(transactions);
        } else {
            log.debug("Transaction data was null.");
            return null;
        }
    }

    /**
     * Builds out a transactions message to be sent to the group.
     *
     * @param transactions transactions data list
     */
    private static String buildTransactionsString(ArrayList<Transaction> transactions) {
        if (transactions.size() != 0) {
            log.debug("Building out transactions message.");

            Collections.sort(transactions);

            final StringBuilder builder = new StringBuilder();

            builder.append("===ALERT===\\n\\n");

            for (Transaction t : transactions) {
                builder.append(t.getTransactionString());
            }

            return builder.toString();
//            ServicesHandler.sendMessage(builder.toString());
        } else {
            log.debug("There are no transactions.  No message has been sent.");
        }

        return null;
    }

    /**
     * Saves token data to DB.
     */
    public static void saveAuthenticationData() {
        Postgres.saveTokenData(currentToken);
    }

    /**
     * Parses standings data.
     *
     * @param doc the data to be parsed
     * @return list of standings data
     */
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
            log.error("Doc was null.");
        }

        return null;
    }

    /**
     * Parses matchup data.
     *
     * @param doc       the data to parse
     * @param standings the standings data of the league
     * @return list of matchups
     */
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
            log.error("Doc was null", new Throwable());
        }

        return null;
    }

    /**
     * Gets score alert data.
     *
     * @param type the type of the score alert to get
     */
    public static String getScoreAlerts(YahooEnum type) {
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
                    log.debug("There were no close scores.  Not sending messages.");
                    return null; // TODO: !!!!!!!!!!!!!!!!
                }
            }

            return message.toString();
//            ServicesHandler.sendMessage(message.toString());
        } else {
            log.debug("Matchups were null.  Not sending message.");
            return null;
        }
    }

    /**
     * Authenticates with the Yahoo servers.
     */
    public static void authenticate() {
        while (true) {
            try {
                final PostgresToken token = Postgres.getLatestTokenData();
                if (token != null) {
                    currentToken = token.getToken();

                    final Long dateRetrieved = token.getRetrievedTime();

                    if (isTokenExpired(dateRetrieved, currentToken.getExpiresIn())) {
                        refreshExpiredToken();
                    }

                    return;
                } else {
                    log.debug("Token does not exist in DB.  Will check again.");
                }
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error(e.getLocalizedMessage());
            }
        }
    }

    /**
     * Grabs the data for the specified url.
     *
     * @param url the url to grab data from
     * @return XML document
     */
    private static Document grabData(String url) {
        try {
            authenticate();
            log.debug("Grabbing Data...");
            final OAuthRequest request = new OAuthRequest(Verb.GET, url);
            service.signRequest(currentToken, request);
            final Response response = service.execute(request);
            log.debug("Data grabbed.");

            return Jsoup.parse(response.getBody(), "", Parser.xmlParser());
        } catch (IOException | InterruptedException | ExecutionException e) {
            log.error(e.getLocalizedMessage());
            return null;
        }
    }

    public static String getListOfTeams() {
        final Document doc = grabData(BASE_URL + "league/" + LEAGUE_KEY + "/teams");

        if (doc != null) {
            final StringBuilder builder = new StringBuilder("Here is a list of teams in the league:\\n\\n");
            final Elements teams = doc.select("team");

            for (Element e : teams) {
                final String number = e.select("team_id").text();
                final String name = e.select("name").text();

                builder.append(number).append(". ").append(name).append("\\n");
            }

            return builder.toString();
        } else {
            return "ERROR: I could not connect to Yahoo's servers.  Please try again later.";
        }
    }

    public static String getTeamRecord(String teamNumber) {
        final Document doc = grabData(BASE_URL + "team/" + LEAGUE_KEY + ".t." + teamNumber + "/standings");

        if (doc != null) {
            final String error = doc.select("error").first().text();
            if (error.isEmpty()) {
                final String name = doc.select("name").text();

                final Element standings = doc.select("team_standings").first();

                final String rank = standings.select("rank").text();
                final String clinchedPlayoffs = standings.select("clinched_playoffs").text();

                final Element overallRecord = standings.select("outcome_totals").first();
                final String oWins = overallRecord.select("wins").text();
                final String oLosses = overallRecord.select("losses").text();
                final String oTies = overallRecord.select("ties").text();

                final Element divisionRecord = standings.select("divisional_outcome_totals").first();
                final String dWins = divisionRecord.select("wins").text();
                final String dLosses = divisionRecord.select("losses").text();
                final String dTies = divisionRecord.select("ties").text();

                final Element streak = standings.select("streak").first();
                final String streakData = streak.select("value").text() + streak.select("type").text().substring(0, 1).toUpperCase();

                return name + "\\n\\n- Rank: " + rank + "\\n- Clinched Playoffs: " + (clinchedPlayoffs.equals("1") ? "Yes" : "No") + "\\n- Overall Record: " + oWins + "-" + oLosses + "-" + oTies + "\\n- Divisional Record: " + dWins + "-" + dLosses + "-" + dTies + "\\n- Streak: " + streakData;
            } else {
                return "ERROR: " + error;
            }
        } else {
            return "ERROR: I could not connect to Yahoo's servers.  Please try again later.";
        }
    }

    public static String getTeamPlayers(String teamNumber) {
        final Document doc = grabData(BASE_URL + "team/" + LEAGUE_KEY + ".t." + teamNumber + "/roster");

        if (doc != null) {
            final String error = doc.select("error").first().text();
            if (error.isEmpty()) {
                final Elements players = doc.select("player");



                for(Element player : players) {

                }

            } else {
                return "ERROR: " + error;
            }
        } else {
            return "ERROR: I could not connect to Yahoo's servers.  Please try again later.";
        }
    }

    private static class Matchup {
        private final String teamOne;
        private final String teamTwo;
        private final YahooTeam teamOneStats;
        private final YahooTeam teamTwoStats;

        private double winProbabilityTeamOne;
        private double projectedPointsTeamOne;
        private double currentScoreTeamOne;
        private double winProbabilityTeamTwo;
        private double projectedPointsTeamTwo;
        private double currentScoreTeamTwo;

        public Matchup(String teamOne, String teamTwo, YahooTeam teamOneStats, YahooTeam teamTwoStats) {
            this.teamOne = teamOne;
            this.teamTwo = teamTwo;
            this.teamOneStats = teamOneStats;
            this.teamTwoStats = teamTwoStats;
        }

        /**
         * Adds matchup data to certain entity.
         *
         * @param winProbability  the probability of winning
         * @param projectedPoints the projected points
         * @param currentScore    the current score of the team
         * @param teamName        the name of the team
         */
        public void addMatchUpData(String winProbability, String projectedPoints, String currentScore, String teamName) {
            if (teamName.equals(teamOne)) {
                this.winProbabilityTeamOne = Double.parseDouble(winProbability);
                this.projectedPointsTeamOne = Double.parseDouble(projectedPoints);
                this.currentScoreTeamOne = Double.parseDouble(currentScore);
            } else {
                this.winProbabilityTeamTwo = Double.parseDouble(winProbability);
                this.projectedPointsTeamTwo = Double.parseDouble(projectedPoints);
                this.currentScoreTeamTwo = Double.parseDouble(currentScore);
            }

        }

        /**
         * Checks to see if the score is close or not.
         *
         * @return is the score close
         */
        public boolean isScoreClose() {
            if (currentScoreTeamOne - currentScoreTeamTwo == 0.0) {
                return false;
            }
            return Math.abs(currentScoreTeamOne - currentScoreTeamTwo) <= 15;
        }

        /**
         * Gets the match data.
         *
         * @param teamName the name of the team
         * @return string of match data
         */
        private String matchData(String teamName) {
            if (teamName.equals(teamOne)) {
                return "- Win Probability: " + winProbabilityTeamOne + "\\n- Projected Points: " + projectedPointsTeamOne;
            } else {
                return "- Win Probability: " + winProbabilityTeamTwo + "\\n- Projected Points: " + projectedPointsTeamTwo;
            }
        }

        /**
         * Weekly matchup alert data.
         *
         * @return string of weekly matchup data
         */
        public String weeklyMatchupAlert() {
            return teamOne + " vs. " + teamTwo + "\\n\\n" + teamOne + "\\n" + matchData(teamOne) + "\\n" + teamOneStats.getStandingsInformation() + "\\n\\n" + teamTwo + "\\n" + matchData(teamTwo) + "\\n" + teamTwoStats.getStandingsInformation();
        }

        /**
         * Scoreboard alert data.
         *
         * @return string of scoreboard alert data
         */
        public String scoreboardAlert() {
            return teamOne + " vs. " + teamTwo + "\\n" + currentScoreTeamOne + " - " + currentScoreTeamTwo;
        }
    }

    public static class YahooTeam {
        private final String rank;
        private final String wins;
        private final String losses;
        private final String ties;
        private final String streak;
        private final String pointsFor;
        private final String pointsAgainst;


        public YahooTeam(String rank, String wins, String losses, String ties, String streak, String pointsFor, String pointsAgainst) {
            this.rank = rank;
            this.wins = wins;
            this.losses = losses;
            this.ties = ties;
            this.streak = streak;
            this.pointsFor = pointsFor;
            this.pointsAgainst = pointsAgainst;
        }

        /**
         * Returns the standings information in a nice format
         * @return standings data
         */
        public String getStandingsInformation() {
            return "- Rank: " + rank + "\\n- Record: " + wins + "-" + losses + "-" + ties + "\\n- Streak: " + streak + "\\n- Points For: " + pointsFor + "\\n- Points Against: " + pointsAgainst;
        }
    }


}
