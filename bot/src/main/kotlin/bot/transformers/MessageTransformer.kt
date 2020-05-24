package bot.transformers

import bot.messaging_services.Message
import io.reactivex.rxjava3.core.Observable

fun Observable<Message>.convertToStringMessage(): Observable<String> =
    map {
        when (it) {
            is Message.Transaction.Add -> createMessage("\uD83D\uDCE3 <b>ADD ALERT</b>\\n━━━━━━━━━", it)
            is Message.Transaction.Drop -> createMessage("\uD83D\uDCE3 <b>DROP ALERT</b>\\n━━━━━━━━━━", it)
            is Message.Transaction.AddDrop -> createMessage("\uD83D\uDCE3 <b>ADD/DROP ALERT</b>\\n━━━━━━━━━━━━━━", it)
            is Message.Transaction.Trade -> createMessage("\uD83D\uDCE3 <b>TRADE ALERT</b>\\n━━━━━━━━━━", it)
            is Message.Transaction.Commish -> createMessage("\uD83D\uDCE3 <b>COMMISH ALERT</b>️\\n━━━━━━━━━━", it)
            is Message.Standings -> createMessage("\uD83D\uDCE3 <b>STANDINGS ALERT</b>\\n━━━━━━━━━", it)
            is Message.Score -> createMessage("\uD83D\uDCE3 <b>SCORE ALERT</b>\\n━━━━━━━━━", it)
            is Message.CloseScore -> createMessage("\uD83D\uDCE3 <b>CLOSE SCORE ALERT</b>\\n━━━━━━━━━", it)
            is Message.MatchUp -> createMessage("\uD83D\uDCE3 <b>MATCHUP ALERT</b>\\n━━━━━━━━━", it)
            is Message.Generic -> createMessage(null, it)
            is Message.Unknown -> createMessage(null, it)
        }
    }.filter {
        it.isNotEmpty()
    }

private fun createMessage(alertTitle: String?, message: Message): String {
    return alertTitle?.let {
        "$it\\n${message.message}"
    } ?: message.message
}
