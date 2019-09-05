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
        val name = it.select("name").first().text()
        val divisionId = it.select("division_id").first().text()
        val waiverPriority = it.select("waiver_priority").first().text()
        val faab = it.select("faab_balance").first().text()
        val numberOfMoves = it.select("number_of_moves").first().text()
        val numberOfTrades = it.select("number_of_trades").first().text()
        val clinchedPlayoffs = it.select("clinched_playoffs").first()?.let { "Yes" } ?: "No"

        val teamStandings = it.select("team_standings").first()
        val rank = teamStandings.select("rank").first().text()

        val outcomeTotals = teamStandings.select("outcome_totals")
        val wins = outcomeTotals.select("wins").first().text()
        val losses = outcomeTotals.select("losses").first().text()
        val ties = outcomeTotals.select("ties").first().text()
        val wltPercentage = outcomeTotals.select("percentage").first().text()

        var streakType: String? = null
        var streakAmount: String? = null
        val streak = teamStandings.select("streak").first()?.let { streak ->
            streakType = if (streak.select("type").first().text() == "win") "W" else "L"
            streakAmount = streak.select("value").first().text()
        }

        val pointsFor = teamStandings.select("points_for").first().text()
        val pointsAgainst = teamStandings.select("points_against").first().text()

        val finalMessage = "Team: $name\\n" +
                "Rank: ${if(rank.isEmpty()) "N/A" else rank}\\n" +
                "Record: $wins-$losses-$ties\\n" +
                "Win %: ${if (wltPercentage.isEmpty()) "N/A" else wltPercentage}\\n" +
                "Streak: ${if (streak == null) "N/A" else "$streakAmount$streakType"}\\n" +
                "Points For: $pointsFor\\n" +
                "Points Against: $pointsAgainst\\n" +
                "Division ID: $divisionId\\n" +
                "Waiver Priority: $waiverPriority\\n" +
                "FAAB: $$faab\\n" +
                "Total Moves: $numberOfMoves\\n" +
                "Total Trades: $numberOfTrades\\n" +
                "Clinched?: $clinchedPlayoffs"

        Message.Standings(finalMessage)
    }
