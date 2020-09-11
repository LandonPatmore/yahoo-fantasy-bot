package bot.transformers

import bot.messaging.Message
import io.reactivex.rxjava3.core.Observable

fun Observable<Message>.convertToStringMessage(): Observable<String> =
    filter {
        it !is Message.Unknown
    }.map {
        createMessage(createTitle(it.title), it.message)
    }.filter {
        it.isNotEmpty()
    }

private fun createTitle(title: String?): String? {
    return title?.let {
        "\uD83D\uDCE3 <b>${it} Alert</b>\\n━━━━━━━━━".toUpperCase()
    }
}

private fun createMessage(alertTitle: String?, message: String): String {
    return alertTitle?.let {
        "$it\\n$message"
    } ?: message
}
