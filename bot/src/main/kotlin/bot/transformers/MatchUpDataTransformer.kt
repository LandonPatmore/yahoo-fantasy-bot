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

        val aheadTeam = if (teamOne.points > teamTwo.points) teamOne else teamTwo
        val behindTeam = if (aheadTeam == teamTwo) teamOne else teamTwo

        Pair(aheadTeam, behindTeam)
    }

fun Observable<Pair<Team, Team>>.convertToMatchUpMessage(): Observable<Message> =
    map {
        val teamDataBuilder = StringBuilder()
        teamDataBuilder.append("<b>${it.first.name}</b> (${it.first.manager}) vs. <b>${it.second.name}</b> (${it.second.manager})\\n")
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
            if (abs(it.first.points - it.second.points) != 0.0) {
                abs(it.first.points - it.second.points) <= 15
            } else {
                false
            }
        } else {
            true
        }
    }.map {
        val ignoreCase = true
        val messageBuilder = StringBuilder()
        // messageBuilder.append("💯 *SCORE ALERT* 📈\n⋯⋯⋯⋯⋯⋯⋯⋯⋯⋯\n")

        messageBuilder.append("> <b>${it.first.name}</b>")
        if (!it.first.name.contains(it.first.manager, ignoreCase)) {
            messageBuilder.append(" (${it.first.manager})")
        }

        messageBuilder.append(" 🆚 ")

        messageBuilder.append("<b>${it.second.name}</b>")
        if (!it.second.name.contains(it.second.manager, ignoreCase)) {
            messageBuilder.append(" (${it.second.manager})")
        }

        messageBuilder.append("\\n> ${it.first.points} – ${it.second.points}")

        val message = messageBuilder.toString().trim()

        if (closeScoreUpdate) {
            Message.CloseScore(message)
        } else {
            Message.Score(message)
        }
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
