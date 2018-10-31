package entities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class Transaction {

    private final String teamOne;
    private final String teamTwo;
    private final String date;
    private final ArrayList<String> teamOnePlayers;
    private final ArrayList<String> teamTwoPlayers;


    public Transaction(String teamOne, String teamTwo, String date) {
        this.teamOne = teamOne;
        this.teamTwo = teamTwo;
        this.date = LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(date)), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("M/d/yyyy - h:mm a", Locale.US));
        this.teamOnePlayers = new ArrayList<>();
        this.teamTwoPlayers = new ArrayList<>();
    }

    private String listString(ArrayList<String> list) {
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() == 2) {
            return list.get(0) + " and " + list.get(1);
        } else if (list.size() > 2) {
            final StringBuilder builder = new StringBuilder();

            for (int i = 0; i < list.size() - 1; i++) {
                builder.append(list.get(i)).append(", ");
            }
            builder.append(" and ").append(list.get(list.size() - 1));

            return builder.toString();
        }

        return "";
    }

    public void addPlayerToTeam(String player, String teamName) {
        if (teamName.equals(teamOne)) {
            teamOnePlayers.add(player);
        } else {
            teamTwoPlayers.add(player);
        }
    }

    public String getTransactionString() {
        return "===TRADE ALERT===\nTrade finalized on: " + date + "\n\n" + teamOne + " acquired: " + listString(teamTwoPlayers) + ".\n\n" +
                teamTwo + " acquired: " + listString(teamOnePlayers) + ".";
    }
}
