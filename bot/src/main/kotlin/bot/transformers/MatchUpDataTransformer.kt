package bot.transformers

import io.reactivex.Observable
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import bot.messaging_services.Message
import java.text.DecimalFormat
import kotlin.math.abs

fun Observable<Document>.convertToMatchUpObject(): Observable<Pair<Team, Team>> =
    flatMapIterable {
        it.select("matchup")
    }.map {
        val teams = it.select("team")
        val teamOne = generateTeamData(teams[0])
        val teamTwo = generateTeamData(teams[1])

        val aheadTeam = if (teamOne.points > teamTwo.points) teamOne else teamTwo
        val behindTeam = if (aheadTeam == teamTwo) teamOne else teamTwo

        Pair(aheadTeam, behindTeam)
    }

fun Observable<Pair<Team, Team>>.convertToMatchUpMessage(): Observable<Message> =
    map {
        val teamDataBuilder = StringBuilder()

        val teams = it.toList()
        for (team: Team in teams) {
            teamDataBuilder.append("> ${generateTeamName(team)}\\n")
            teamDataBuilder.append("> • Win Probability: <b>${DecimalFormat("#.##").format(team.winProbability)}%</b>\\n")
            teamDataBuilder.append("> • Projected Points: <b>${team.projectedPoints}</b>\\n")
            if (teams.indexOf(team) == 0) {
                teamDataBuilder.append("> 🆚\\n")
            }
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
        val messageBuilder = StringBuilder()

        val header = generateMatchUpHeader(it.first, it.second)
        messageBuilder.append(header)

        messageBuilder.append("> ")
        messageBuilder.append("${it.first.points} (proj: ${it.first.projectedPoints})")
        messageBuilder.append(" - ")
        messageBuilder.append("${it.second.points} (proj: ${it.second.projectedPoints})")

        val message = messageBuilder.toString().trim()

        if (closeScoreUpdate) {
            Message.CloseScore(message)
        } else {
            Message.Score(message)
        }
    }

private fun generateTeamName(team: Team): String {
    val ignoreCase = true
    val name = StringBuilder()

    name.append("<b>${team.name}</b>")
    if (!team.name.contains(team.manager, ignoreCase) && !team.manager.contains("hidden")) {
        name.append(" (${team.manager})")
    }

    return name.toString().trim()
}

private fun generateMatchUpHeader(teamOne: Team, teamTwo: Team): String {
    val header = StringBuilder()

    header.append("> ${generateTeamName(teamOne)}")
    header.append(" 🆚 ")
    header.append("${generateTeamName(teamTwo)}")
    header.append("\\n")

    return header.toString().trim()
}

private fun generateTeamData(team: Element): Team {
    val id = team.select("team_id").text().toInt()
    val name = team.select("name").text()
    val manager = team.select("managers").select("manager").first().select("nickname").text()
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
        manager,
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
    val manager: String,
    val waiverPriority: Int?,
    val faabBalance: Int?,
    val numberOfMoves: Int,
    val numberOfTrades: Int,
    val winProbability: Double,
    val points: Double,
    val projectedPoints: Double
)
