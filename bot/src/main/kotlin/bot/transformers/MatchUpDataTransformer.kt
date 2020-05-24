package bot.transformers

import bot.messaging_services.Message
import io.reactivex.rxjava3.core.Observable
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.text.DecimalFormat
import kotlin.math.abs

fun Observable<Document>.convertToMatchUpObject(): Observable<Pair<Team, Team>> =
    flatMapIterable {
        it.select("matchup")
    }.map {
        val teams = it.select("team")

        Pair(generateTeamData(teams[0]), generateTeamData(teams[1]))
    }

fun Observable<Pair<Team, Team>>.convertToMatchUpMessage(): Observable<Message> =
    map {
        val teamDataBuilder = StringBuilder()

        val teams = it.toList()
        for (team: Team in teams) {
            teamDataBuilder.append(
                """
                |> ${generateTeamName(team)}
                |> • Win Probability: <b>${DecimalFormat("#.##").format(team.winProbability)}%</b>
                |> • Projected Points: <b>${team.projectedPoints}</b>
                |${
                if (teams.indexOf(team) == 0) {
                    "> \uD83C\uDD9A"
                } else {
                    ""
                }}
            """.trimMargin()
            )
        }

        Message.MatchUp(teamDataBuilder.toString())
    }

fun Observable<Pair<Team, Team>>.convertToScoreUpdateMessage(closeScoreUpdate: Boolean = false): Observable<Message> =
    filter {
        if (closeScoreUpdate) {
            abs(it.first.winProbability - it.second.winProbability) < 40.0
        } else {
            true
        }
    }.map {
        val message = "${it.first.name} \uD83C\uDD9A ${it.second.name}\\n" +
                "${it.first.points} (${it.first.projectedPoints})" +
                " - " +
                "${it.second.points} (${it.second.projectedPoints})"

        if (closeScoreUpdate) {
            Message.CloseScore(message)
        } else {
            Message.Score(message)
        }
    }

private fun generateTeamName(team: Team): String {
    return "<b>${team.name}</b> ${
    if (!team.name.contains(team.manager, true) && !team.manager.contains("hidden")) {
        "(${team.manager})"
    } else {
        ""
    }}"
}

private fun generateMatchUpHeader(teamOne: Team, teamTwo: Team): String {
    return "> ${generateTeamName(teamOne)} \uD83C\uDD9A ${generateTeamName(teamTwo)}\\n"
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
