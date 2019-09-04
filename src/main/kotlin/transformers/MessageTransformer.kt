package transformers

import io.reactivex.Observable
import types.Message

fun Observable<Message>.convertToStringMessage(): Observable<String?> =
    map {
        when (it) {
            is Message.Transaction.Add -> createMessage("===ADD ALERT===", it)
            is Message.Transaction.Drop -> createMessage("===DROP ALERT===", it)
            is Message.Transaction.AddDrop -> createMessage("===ADD/DROP ALERT===", it)
            is Message.Transaction.Trade -> createMessage("===TRADE ALERT===", it)
            is Message.Transaction.Commish -> createMessage("===COMMISH ALERT===", it)
            is Message.Standings -> createMessage("===STANDINGS ALERT===", it)
            is Message.ScoreUpdate -> createMessage("===SCORE UPDATE===", it)
            is Message.CloseScoreUpdate -> createMessage("===CLOSE SCORE UPDATE===", it)
            is Message.MatchUp -> createMessage("===MATCH UP ALERT===", it)
        }
    }

private fun createMessage(alertTitle: String, message: Message): String {
    return "$alertTitle\\n\\n${message.message}"
}