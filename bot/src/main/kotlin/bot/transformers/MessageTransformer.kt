package bot.transformers

import io.reactivex.Observable
import bot.messaging_services.Message

fun Observable<Message>.convertToStringMessage(): Observable<String> =
    map {
        when (it) {
            is Message.Transaction.Add -> createMessage(":mega: <b>ADD ALERT</b> :chart_with_upwards_trend:\\n━━━━━━━━━", it)
            is Message.Transaction.Drop -> createMessage(":mega: <b>DROP ALERT</b> :chart_with_downwards_trend:\\n━━━━━━━━━━", it)
            is Message.Transaction.AddDrop -> createMessage(":mega: <b>ADD/DROP ALERT</b> :chart_with_upwards_trend::chart_with_downwards_trend:\\n━━━━━━━━━━━━━━", it)
            is Message.Transaction.Trade -> createMessage(":mega: <b>TRADE ALERT</b> :twisted_rightwards_arrows:\\n━━━━━━━━━━", it)
            is Message.Transaction.Commish -> createMessage(":mega: <b>COMMISH ALERT</b> :raising_hand:\\n━━━━━━━━━━", it)
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
        "$alertTitle\\n${message.message}"
    } ?: message.message
}
