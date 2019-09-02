package transformers

import io.reactivex.Observable
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

fun Observable<Document>.convertToTransactionMessage(lastTimeChecked: Long): Observable<String> =
    flatMapIterable {
        it.select("transaction")
    }.filter {
        it.select("timestamp").first().text().toLong() >= lastTimeChecked
    }.map {
        when (it.select("type").first().text()) {
            "add" -> addMessage(it)
            "drop" -> dropMessage(it)
            "add/drop" -> addDropMessage(it)
            "trade" -> tradeMessage(it)
            "commish" -> commissionerMessage()
            else -> null
        }
    }

private fun addMessage(event: Element): String {
    val fantasyTeam = event.select("destination_team_name").text()
    val players = event.select("player")

    val playersAdded = StringBuilder()

    for (player: Element in players) {
        val name = player.select("full").text()
        val nflTeam = player.select("editorial_team_abbr").text()
        val position = player.select("display_position").text()

        playersAdded.append("$name ($nflTeam, $position), ")
    }

    val finalMessage = playersAdded.trimEnd().removeSuffix(",")

    return "ADD ALERT\n\n" +
            "transformers.Team: $fantasyTeam\n" +
            "${if (players.size > 1) "Players" else "Player"}: $finalMessage"
}

private fun dropMessage(event: Element): String {
    val fantasyTeam = event.select("source_team_name").text()
    val players = event.select("player")

    val playersDropped = StringBuilder()


    for (player: Element in players) {
        val name = player.select("full").text()
        val nflTeam = player.select("editorial_team_abbr").text()
        val position = player.select("display_position").text()

        playersDropped.append("$name ($nflTeam, $position), ")
    }

    val finalMessage = playersDropped.trimEnd().removeSuffix(",")

    return "DROP ALERT\n\n" +
            "transformers.Team: $fantasyTeam\n" +
            "${if (players.size > 1) "Players" else "Player"}: $finalMessage"
}

private fun addDropMessage(event: Element): String {
    val fantasyTeam = event.select("source_team_name").text()
    val players = event.select("player")

    val playersAdded = StringBuilder()
    val playersDropped = StringBuilder()

    var playersAddedCount = 0
    var playersDroppedCount = 0

    for (player: Element in players) {
        val name = player.select("full").text()
        val nflTeam = player.select("editorial_team_abbr").text()
        val position = player.select("display_position").text()

        val e = "$name ($nflTeam, $position), "

        if (player.select("type").text() == "add") {
            playersAdded.append(e)
            playersAddedCount++
        } else {
            playersDropped.append(e)
            playersDroppedCount++
        }
    }

    val finalMessageAdded = playersAdded.trimEnd().removeSuffix(",")
    val finalMessageDropped = playersDropped.trimEnd().removeSuffix(",")

    return "ADD/DROP ALERT\n\n" +
            "transformers.Team: $fantasyTeam\n" +
            "Added ${if (playersAddedCount > 1) "Players" else "Player"}: $finalMessageAdded\n" +
            "Dropped ${if (playersDroppedCount > 1) "Players" else "Player"}: $finalMessageDropped"
}

private fun tradeMessage(event: Element): String {
    val trader = event.select("trader_team_name").text()
    val tradee = event.select("tradee_team_name").text()

    val players = event.select("player")

    val fromTraderTeam = StringBuilder()
    val fromTradeeTeam = StringBuilder()

    for (player: Element in players) {
        val fantasyTeam = player.select("source_team_name").text()
        val name = player.select("full").text()
        val nflTeam = player.select("editorial_team_abbr").text()
        val position = player.select("display_position").text()

        val e = "$name ($nflTeam, $position), "

        if (fantasyTeam == trader) {
            fromTraderTeam.append(e)
        } else {
            fromTradeeTeam.append(e)
        }
    }

    val finalMessageFromTrader = fromTraderTeam.trimEnd().removeSuffix(",")
    val finalMessageFromTradee = fromTradeeTeam.trimEnd().removeSuffix(",")

    return "TRADE ALERT\n\n" +
            "$trader received: $finalMessageFromTradee\n" +
            "$tradee received: $finalMessageFromTrader"
}

private fun commissionerMessage(): String {
    return "COMMISSIONER ALERT\n\n" +
            "A league setting has been modified.  You may want to check or ask them what they changed!"
}