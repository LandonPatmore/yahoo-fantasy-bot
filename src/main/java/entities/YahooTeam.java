package entities;

public class YahooTeam {
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

    public String getStandingsInformation() {
        return "- Rank: " + rank + "\\n- Record: " + wins + "-" + losses + "-" + ties + "\\n- Streak: " + streak + "\\n- Points For: " + pointsFor + "\\n- Points Against: " + pointsAgainst;
    }
}
