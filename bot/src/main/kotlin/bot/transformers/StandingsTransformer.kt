package bot.transformers

import io.reactivex.Observable
import bot.messaging_services.Message
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
            if (div.isEmpty()) null else div
        }
        val waiverPriority = it.select("waiver_priority").text().toIntOrNull()
        val faab = it.select("faab_balance").text().toIntOrNull()
        val numberOfMoves = it.select("number_of_moves").text()
        val numberOfTrades = it.select("number_of_trades").text()
        val clinchedPlayoffs = it.select("clinched_playoffs").text()?.let { cp ->
            if (cp.isEmpty() || cp == "0") null else "Yes"
        }

        val teamStandings = it.select("team_standings")
        val rank = teamStandings.select("rank").text()?.let { rank ->
            if (rank.isEmpty() || rank == "0") 0 else rank.toInt()
        } ?: 0

        val outcomeTotals = teamStandings.select("outcome_totals")
        val wins = outcomeTotals.select("wins").text()
        val losses = outcomeTotals.select("losses").text()
        val ties = outcomeTotals.select("ties").text()
        val wltPercentage = DecimalFormat("#.##").format(outcomeTotals.select("percentage").text().toDouble())

        var streakType: String? = null
        var streakAmount: Int = 0
        teamStandings.select("streak")?.let { streak ->
            streakType = if (streak.select("type").text() == "win") "W" else "L"
            streakAmount = streak.select("value").text().toInt()
        }

        val pointsFor = teamStandings.select("points_for").text()
        val pointsAgainst = teamStandings.select("points_against").text()

        val messageBuilder = StringBuilder()

        messageBuilder.append("> ${numberToEmoji(rank)} ${generateTeamName(name, manager)}\\n")
        messageBuilder.append("> • Record: <b>$wins-$losses-$ties</b>\\n")
        messageBuilder.append("> • Win %: <b>$wltPercentage</b>\\n")
        if (streakAmount > 1) messageBuilder.append("> • Streak: <b>$streakAmount$streakType</b>\\n")
        messageBuilder.append("> • Points For: <b>$pointsFor</b>, Against: <b>$pointsAgainst</b>\\n")
        divisionId?.let { messageBuilder.append("> • Division ID: <b>$divisionId</b>\\n") }
        messageBuilder.append(
            faab?.let {
                "> • FAAB: <b>$faab</b>\\n"
            } ?: "> • Waiver Priority: <b>$waiverPriority</b>\\n"
        )
        clinchedPlayoffs?.let { messageBuilder.append("> • Clinched?: <b>$clinchedPlayoffs</b>") }

        val finalMessage = messageBuilder.toString().trim()
        Message.Standings(finalMessage)
    }

private fun generateTeamName(team: String, manager: String): String {
    val ignoreCase = true
    val name = StringBuilder()

    name.append("<b>${team}</b>")
    if (!team.contains(manager, ignoreCase) && !manager.contains("hidden")) {
        name.append(" (${manager})")
    }

    return name.toString().trim()
}

private fun numberToEmoji(number: Int): String {
    when(number) {
        0 -> return ":zero:"
        1 -> return ":one:"
        2 -> return ":two:"
        3 -> return ":three:"
        4 -> return ":four:"
        5 -> return ":five:"
        6 -> return ":six:"
        7 -> return ":seven:"
        8 -> return ":eight:"
        9 -> return ":nine:"
        10 -> return ":one::zero:"
        11 -> return ":one::one:"
        12 -> return ":one::two:"
        13 -> return ":one::three:"
        14 -> return ":one::four:"
        15 -> return ":one::five:"
        16 -> return ":one::six:"
        17 -> return ":one::seven:"
        18 -> return ":one::eight:"
        19 -> return ":one::nine:"
        else -> return ":${number.toString()}:"
    }
}
