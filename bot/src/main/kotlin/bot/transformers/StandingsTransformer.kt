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

package bot.transformers

import bot.messaging.Message
import bot.utils.toPercentage
import io.reactivex.rxjava3.core.Observable
import org.jsoup.nodes.Document

fun Observable<Document>.convertToStandingsMessage(): Observable<Message> =
    flatMapIterable {
        it.select("team")
    }.map {
        val name = it.select("name").text()
        val manager =
            it.select("managers").select("manager").first().select("nickname")
                .text()
        val waiverPriority = it.select("waiver_priority").text().toIntOrNull()
        val faab = it.select("faab_balance").text().toIntOrNull()
        val clinchedPlayoffs =
            it.select("clinched_playoffs").text()?.let { cp ->
                if (cp.isEmpty() || cp == "0") {
                    null
                } else {
                    "Yes"
                }
            }

        val teamStandings = it.select("team_standings")
        val rank = teamStandings.select("rank").text()?.let { rank ->
            if (rank.isEmpty()) {
                0
            } else {
                rank.toInt()
            }
        } ?: 0

        val outcomeTotals = teamStandings.select("outcome_totals")
        val wins = outcomeTotals.select("wins").text()
        val losses = outcomeTotals.select("losses").text()
        val ties = outcomeTotals.select("ties").text()
        val wltPercentage =
            outcomeTotals.select("percentage").text().toDouble().toPercentage()

        var streakType = ""
        var streakAmount = 0
        teamStandings.select("streak")?.let { streak ->
            streakType = if (streak.select("type").text() == "win") {
                "W"
            } else {
                "L"
            }
            streakAmount = streak.select("value").text().toInt()
        }

        val pointsFor = teamStandings.select("points_for").text()
        val pointsAgainst = teamStandings.select("points_against").text()

        Message.Standings("""
            |$rank ${generateTeamName(name, manager)}
            |Record: <b>$wins-$losses-$ties</b>
            |Win %: <b>$wltPercentage</b>
            |${
            if (streakAmount > 1) {
                "Streak: <b>$streakAmount$streakType</b>"
            } else {
                ""
            }
        }
            |Points For: <b>$pointsFor</b>, Against: <b>$pointsAgainst</b>
            |${
            faab?.let {
                "FAAB: <b>$faab</b>"
            } ?: "Waiver Priority: <b>$waiverPriority</b>"
        }
        |${
            clinchedPlayoffs?.let {
                "Clinched?: <b>$clinchedPlayoffs</b>"
            }
        }
        """.trimMargin()
        )
    }

private fun generateTeamName(team: String, manager: String): String {
    return """
        |<b>$team</b> ${
        if (!team.contains(manager, true) && !manager.contains("hidden")) {
            "($manager)"
        } else {
            ""
        }
    }
    """.trimMargin()
}
