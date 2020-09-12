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
import io.reactivex.rxjava3.core.Observable
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

fun Observable<Pair<Long, Document>>.convertToTransactionMessage(): Observable<Message> =
    flatMap {
        Observable.fromIterable(it.second.select("transaction"))
            .map { transaction ->
                Pair(it.first, transaction)
            }
    }.filter {
        it.second.select("timestamp").first().text().toLong() >= it.first
    }.map {
        when (it.second.select("type").first().text()) {
            "add" -> addMessage(it.second)
            "drop" -> dropMessage(it.second)
            "add/drop" -> addDropMessage(it.second)
            "trade" -> tradeMessage(it.second)
            "commish" -> commissionerMessage()
            else -> Message.Unknown("")
        }
    }

private fun addMessage(event: Element): Message {
    val fantasyTeam = event.select("destination_team_name").text()

    return Message.Transaction.Add(
        """
            |<b>$fantasyTeam</b>
            |Added: ${getPlayerInfo(event)}
        """.trimMargin()
    )
}

private fun dropMessage(event: Element): Message {
    val fantasyTeam = event.select("source_team_name").text()

    return Message.Transaction.Drop(
        """
            |<b>$fantasyTeam</b>
            |Dropped: ${getPlayerInfo(event)}
        """.trimMargin()
    )
}

private fun addDropMessage(event: Element): Message {
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

        val e = "<b>$name</b> ($nflTeam, $position), "

        if (player.select("type").text() == "add") {
            playersAdded.append(e)
            playersAddedCount++
        } else {
            playersDropped.append(e)
            playersDroppedCount++
        }
    }

    return Message.Transaction.AddDrop(
        """
            |<b>$fantasyTeam</b>
            |Added: ${playersAdded.trimEnd().removeSuffix(",")}
            |Dropped: ${playersDropped.trimEnd().removeSuffix(",")}
        """.trimMargin()
    )
}

private fun tradeMessage(event: Element): Message {
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

        val e = "<b>$name</b> ($nflTeam, $position), "

        if (fantasyTeam == trader) {
            fromTraderTeam.append(e)
        } else {
            fromTradeeTeam.append(e)
        }
    }

    val finalMessageFromTrader = fromTraderTeam.trimEnd().removeSuffix(",")
    val finalMessageFromTradee = fromTradeeTeam.trimEnd().removeSuffix(",")

    return Message.Transaction.Trade(
        """
            |<b>$trader</b> traded: $finalMessageFromTradee
            |<b>$tradee</b> traded: $finalMessageFromTrader
        """.trimMargin()
    )
}

private fun commissionerMessage(): Message {
    return Message.Transaction.Commish("A league setting has been modified. You may want to check or ask them what they changed!")
}

private fun getPlayerInfo(event: Element): String {
    val players = event.select("player")

    val playersAdded = StringBuilder()

    for (player: Element in players) {
        val name = player.select("full").text()
        val nflTeam = player.select("editorial_team_abbr").text()
        val position = player.select("display_position").text()

        playersAdded.append("<b>$name</b> ($nflTeam, $position), ")
    }

    return playersAdded.toString().trimEnd().removeSuffix(",")
}
