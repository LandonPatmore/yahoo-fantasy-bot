package bot.transformers

import io.reactivex.Observable
import bot.messaging_services.Message

fun Observable<Message>.convertToStringMessage(): Observable<String> =
    map {
        when (it) {
            is Message.Transaction.Add -> createMessage("📣<b>ADD ALERT</b> 📈\\n━━━━━━━━━", it)
            is Message.Transaction.Drop -> createMessage("<b>DROP ALERT</b> 📉\\n━━━━━━━━━━", it)
            is Message.Transaction.AddDrop -> createMessage("📣<b>ADD/DROP ALERT</b> 📈📉\\n━━━━━━━━━━━━━━", it)
            is Message.Transaction.Trade -> createMessage("📣<b>TRADE ALERT</b> 🔀\\n━━━━━━━━━━", it)
            is Message.Transaction.Commish -> createMessage("📣<b>COMMISH ALERT</b> 🙋‍♀️\\n━━━━━━━━━━", it)
            is Message.Standings -> createMessage(null, it)
            is Message.Score -> createMessage(null, it)
            is Message.CloseScore -> createMessage(null, it)
            is Message.MatchUp -> createMessage(null, it)
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
