package bot.transformers

import io.reactivex.Observable
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import bot.messaging_services.Message
import kotlin.math.abs

fun Observable<Document>.convertToMatchUpObject(): Observable<Pair<Team, Team>> =
    flatMapIterable {
        it.select("matchup")
    }.map {
        val teams = it.select("team")
        val teamOne = generateTeamData(teams[0])
        val teamTwo = generateTeamData(teams[1])

        Pair(teamOne, teamTwo)
    }

fun Observable<Pair<Team, Team>>.convertToMatchUpMessage(): Observable<Message> =
    map {
        val teamDataBuilder = StringBuilder()
        teamDataBuilder.append("${it.first.name} vs. ${it.second.name}\\n")
        teamDataBuilder.append("===\\n")

        val teams = it.toList()
        for (team: Team in teams) {
            teamDataBuilder.append("Team: ${team.name}\\n${"Win Probability: %.2f".format(team.winProbability)}%\\nProjected Points: ${team.projectedPoints}")
            teamDataBuilder.append(
                team.faabBalance?.let { balance ->
                    "\\nFAAB: $balance"
                } ?: "\\nWaiver Priority: ${team.waiverPriority}"
            )
            teamDataBuilder.append("\\n\\n")
        }

        val finalMessage = teamDataBuilder.toString().trim()

        Message.MatchUp(finalMessage)
    }

fun Observable<Pair<Team, Team>>.convertToScoreUpdateMessage(closeScoreUpdate: Boolean = false): Observable<Message> =
    filter {
        if (closeScoreUpdate) {
            abs(it.first.winProbability - it.second.winProbability) < 40.0
        } else {
            true
        }
    }.map {
        val message = "${it.first.name} vs. ${it.second.name}\\n" +
                "${it.first.points} (${it.first.projectedPoints})" +
                " - " +
                "${it.second.points} (${it.second.projectedPoints})"

        if (closeScoreUpdate) {
            Message.CloseScore(message)
        } else {
            Message.Score(message)
        }
    }

private fun generateTeamData(team: Element): Team {
    val id = team.select("team_id").text().toInt()
    val name = team.select("name").text()
    val waiverPriority = team.select("waiver_priority").text().toIntOrNull()
    val faabBalance = team.select("faab_balance").text().toIntOrNull()
    val numberOfMoves = team.select("number_of_moves").text().toInt()
    val numberOfTrades = team.select("number_of_trades").text().toInt()
    val winProbability = team.select("win_probability").text().toDouble() * 100
    val points = team.select("team_points").select("total").text().toDouble()
    val projectedPoints = team.select("team_projected_points").select("total").text().toDouble()

    return Team(
        name,
        id,
        waiverPriority,
        faabBalance,
        numberOfMoves,
        numberOfTrades,
        winProbability,
        points,
        projectedPoints
    )
}

data class Team(
    val name: String,
    val id: Int,
    val waiverPriority: Int?,
    val faabBalance: Int?,
    val numberOfMoves: Int,
    val numberOfTrades: Int,
    val winProbability: Double,
    val points: Double,
    val projectedPoints: Double
)
