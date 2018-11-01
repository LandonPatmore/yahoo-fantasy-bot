package helpers;

public class YahooTeam {
    private final String teamName;
    private final String rank;
    private final String wins;
    private final String losses;
    private final String ties;
    private final String streak;
    private final String pointsFor;
    private final String pointsAgainst;


    public YahooTeam(String teamName, String rank, String wins, String losses, String ties, String streak, String pointsFor, String pointsAgainst) {
        this.teamName = teamName;
        this.rank = rank;
        this.wins = wins;
        this.losses = losses;
        this.ties = ties;
        this.streak = streak;
        this.pointsFor = pointsFor;
        this.pointsAgainst = pointsAgainst;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getRank() {
        return rank;
    }

    public String getWins() {
        return wins;
    }

    public String getLosses() {
        return losses;
    }

    public String getTies() {
        return ties;
    }

    public String getStreak() {
        return streak;
    }

    public String getPointsFor() {
        return pointsFor;
    }

    public String getPointsAgainst() {
        return pointsAgainst;
    }
}
