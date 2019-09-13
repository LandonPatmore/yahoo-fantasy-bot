package bot.transformers

import io.reactivex.Observable
import bot.messaging_services.Message
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

fun Observable<Document>.convertToStandingsObject(): Observable<Element> =
    flatMapIterable {
        it.select("team")
    }

fun Observable<Element>.convertToStandingsMessage(): Observable<Message> =
    map {
        val name = it.select("name").text()
        val divisionId = it.select("division_id").text()
        val waiverPriority = it.select("waiver_priority").text().toIntOrNull()
        val faab = it.select("faab_balance").text().toIntOrNull()
        val numberOfMoves = it.select("number_of_moves").text()
        val numberOfTrades = it.select("number_of_trades").text()
        val clinchedPlayoffs = it.select("clinched_playoffs").text()?.let { cp ->
            if (cp.isEmpty() || cp == "0") "No" else "Yes"
        } ?: "No"

        val teamStandings = it.select("team_standings")
        val rank = teamStandings.select("rank").text()

        val outcomeTotals = teamStandings.select("outcome_totals")
        val wins = outcomeTotals.select("wins").text()
        val losses = outcomeTotals.select("losses").text()
        val ties = outcomeTotals.select("ties").text()
        val wltPercentage = outcomeTotals.select("percentage").text()

        var streakType: String? = null
        var streakAmount: String? = null
        val streak = teamStandings.select("streak")?.let { streak ->
            streakType = if (streak.select("type").text() == "win") "W" else "L"
            streakAmount = streak.select("value").text()
        }

        val pointsFor = teamStandings.select("points_for").text()
        val pointsAgainst = teamStandings.select("points_against").text()

        val finalMessage = "Team: $name\\n" +
                "Rank: ${if (rank.isEmpty()) "N/A" else rank}\\n" +
                "Record: $wins-$losses-$ties\\n" +
                "Win %: ${if (wltPercentage.isEmpty()) "N/A" else wltPercentage}\\n" +
                "Streak: ${if (streak == null) "N/A" else "$streakAmount$streakType"}\\n" +
                "Points For: $pointsFor\\n" +
                "Points Against: $pointsAgainst\\n" +
                "Division ID: $divisionId\\n" +
                (faab?.let {
                    "FAAB: $$faab\\n"
                } ?: "Waiver Priority: $waiverPriority\\n") +
                "Total Moves: $numberOfMoves\\n" +
                "Total Trades: $numberOfTrades\\n" +
                "Clinched?: $clinchedPlayoffs"

        Message.Standings(finalMessage)
    }
