package bot.transformers

import bot.messaging_services.Message
import io.reactivex.rxjava3.core.Observable
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.text.DecimalFormat

fun Observable<Document>.convertToStandingsObject(): Observable<Element> =
    flatMapIterable {
        it.select("team")
    }

fun Observable<Element>.convertToStandingsMessage(): Observable<Message> =
    map {
        val name = it.select("name").text()
        val manager = it.select("managers").select("manager").first().select("nickname").text()
        val divisionId = it.select("division_id").text()?.let { div ->
            if (div.isEmpty()) {
                null
            } else {
                div
            }
        }
        val waiverPriority = it.select("waiver_priority").text().toIntOrNull()
        val faab = it.select("faab_balance").text().toIntOrNull()
        val clinchedPlayoffs = it.select("clinched_playoffs").text()?.let { cp ->
            if (cp.isEmpty() || cp == "0") {
                null
            } else {
                "Yes"
            }
        }

        val teamStandings = it.select("team_standings")
        val rank = teamStandings.select("rank").text()?.let { rank ->
            if (rank.isEmpty() || rank == "0") {
                0
            } else {
                rank.toInt()
            }
        } ?: 0

        val outcomeTotals = teamStandings.select("outcome_totals")
        val wins = outcomeTotals.select("wins").text()
        val losses = outcomeTotals.select("losses").text()
        val ties = outcomeTotals.select("ties").text()
        val wltPercentage = DecimalFormat("#.##").format(outcomeTotals.select("percentage").text().toDouble())

        var streakType: String? = null
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
            |> ${numberToEmoji(rank)} ${generateTeamName(name, manager)}
            |> • Record: <b>$wins-$losses-$ties</b>
            |> • Win %: <b>$wltPercentage</b>
            |${
        if (streakAmount > 1) {
            "> • Streak: <b>$streakAmount$streakType</b>"
        } else {
            ""
        }}
            |> • Points For: <b>$pointsFor</b>, Against: <b>$pointsAgainst</b>
            |${divisionId?.let { "> • Division ID: <b>$divisionId</b>" }}
            |${
        faab?.let {
            "> • FAAB: <b>$faab</b>"
        } ?: "> • Waiver Priority: <b>$waiverPriority</b>"}
        |${clinchedPlayoffs?.let { "> • Clinched?: <b>$clinchedPlayoffs</b>" }}
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
    }}
    """.trimMargin()
}

private fun numberToEmoji(number: Int): String {
    when (number) {
        0 -> return "0️⃣"
        1 -> return "1️⃣"
        2 -> return "2️⃣"
        3 -> return "3️⃣"
        4 -> return "4️⃣"
        5 -> return "5️⃣"
        6 -> return "6️⃣"
        7 -> return "7️⃣"
        8 -> return "8️⃣"
        9 -> return "9️⃣"
        10 -> return "1️⃣0️⃣"
        11 -> return "1️⃣1️⃣"
        12 -> return "1️⃣2️⃣"
        13 -> return "1️⃣3️⃣"
        14 -> return "1️⃣4️⃣"
        15 -> return "1️⃣5️⃣"
        16 -> return "1️⃣6️⃣"
        17 -> return "1️⃣7️⃣"
        18 -> return "1️⃣8️⃣"
        19 -> return "1️⃣9️⃣"
        else -> return ":$number:"
    }
}
