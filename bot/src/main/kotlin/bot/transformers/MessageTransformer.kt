package bot.transformers

import io.reactivex.Observable
import bot.messaging_services.Message

fun Observable<Message>.convertToStringMessage(): Observable<String> =
    map {
        when (it) {
            is Message.Transaction.Add -> createMessage("===ADD ALERT===", it)
            is Message.Transaction.Drop -> createMessage("===DROP ALERT===", it)
            is Message.Transaction.AddDrop -> createMessage("===ADD/DROP ALERT===", it)
            is Message.Transaction.Trade -> createMessage("===TRADE ALERT===", it)
            is Message.Transaction.Commish -> createMessage("===COMMISH ALERT===", it)
            is Message.Standings -> createMessage("===STANDINGS ALERT===", it)
            is Message.Score -> createMessage("===SCORE ALERT===", it)
            is Message.CloseScore -> createMessage("===CLOSE SCORE ALERT===", it)
            is Message.MatchUp -> createMessage("===MATCH UP ALERT===", it)
            is Message.Generic -> createMessage(null, it)
            is Message.Unknown -> createMessage(null, it)
        }
    }.filter {
        it.isNotEmpty()
    }

private fun createMessage(alertTitle: String?, message: Message): String {
    return alertTitle?.let {
        "$alertTitle\\n\\n${message.message}"
    } ?: message.message
}
