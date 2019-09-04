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
            else -> null
        }
    }

private fun createMessage(alertTitle: String, message: Message): String {
    return "$alertTitle\\n\\n${message.message}"
}