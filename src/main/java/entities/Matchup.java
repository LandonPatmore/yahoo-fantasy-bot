package entities;

public class Matchup {
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

    public boolean isScoreClose() {
        if(currentScoreTeamOne - currentScoreTeamTwo == 0.0){
            return false;
        }
        return Math.abs(currentScoreTeamOne - currentScoreTeamTwo) <= 15;
    }

    private String matchData(String teamName) {
        if (teamName.equals(teamOne)) {
            return "- Win Probability: " + winProbabilityTeamOne + "\\n- Projected Points: " + projectedPointsTeamOne;
        } else {
            return "- Win Probability: " + winProbabilityTeamTwo + "\\n- Projected Points: " + projectedPointsTeamTwo;
        }
    }

    public String weeklyMatchupAlert() {
        return teamOne + " vs. " + teamTwo + "\\n\\n" + teamOne + "\\n" + matchData(teamOne) + "\\n" + teamOneStats.getStandingsInformation() + "\\n\\n" + teamTwo + "\\n" + matchData(teamTwo) + "\\n" + teamTwoStats.getStandingsInformation();
    }

    public String scoreboardAlert() {
        return teamOne + " vs. " + teamTwo + "\\n" + currentScoreTeamOne + " - " + currentScoreTeamTwo;
    }
}
