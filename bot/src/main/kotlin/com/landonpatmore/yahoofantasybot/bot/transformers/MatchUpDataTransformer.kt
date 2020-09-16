/*
 * MIT License
 *
 * Copyright (c) 2020 Landon Patmore
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.landonpatmore.yahoofantasybot.bot.transformers

import com.landonpatmore.yahoofantasybot.bot.messaging.Message
import com.landonpatmore.yahoofantasybot.bot.utils.bold
import com.landonpatmore.yahoofantasybot.bot.utils.toPercentage
import io.reactivex.rxjava3.core.Observable
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
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
        teamDataBuilder.append("${it.first.name.bold()} vs. ${it.second.name.bold()}\\n")
        teamDataBuilder.append(
            "${it.first.projectedPoints.bold()} (${it.first.winProbability.toPercentage()}) " +
                    "- ${it.second.projectedPoints.bold()} (${it.second.winProbability.toPercentage()})"
        )

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
        val message = "${it.first.name.bold()} vs. ${it.second.name.bold()}\\n" +
                "${it.first.points.bold()} (${it.first.projectedPoints})" +
                " - " +
                "${it.second.points.bold()} (${it.second.projectedPoints})"

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
