package bot.messaging_services

import com.mashape.unirest.http.exceptions.UnirestException
import io.reactivex.rxjava3.functions.Consumer

abstract class MessagingService(private val maxMessageLength: Int) : Consumer<String> {

    override fun accept(message: String?) {
        message?.let { createMessage(it) }
    }

    @Throws(UnirestException::class)
    protected abstract fun sendMessage(message: String)

    private fun createMessage(message: String) {
        try {
            Thread.sleep(1000)
            if (message.length > maxMessageLength) {
                val subMessage = message.substring(0, maxMessageLength + 1)
                sendMessage(correctMessage(subMessage))
                createMessage(message.substring(maxMessageLength + 1))
            }
            sendMessage(correctMessage(message))
        } catch (e : Exception) {
            println(e.localizedMessage)
        }
    }

    private fun correctMessage(message: String): String {
        val properJsonFormat = message.replace("\n", "\\n")

        return when {
            properJsonFormat.startsWith("n") -> properJsonFormat.substring(1)
            properJsonFormat.startsWith("\\") -> properJsonFormat.substring(1)
            properJsonFormat.startsWith("\\n") -> properJsonFormat.substring(2)
            properJsonFormat.endsWith("\\") -> properJsonFormat.substring(0, message.length)
            properJsonFormat.endsWith("\\n") -> properJsonFormat.substring(0, message.length - 1)
            else -> properJsonFormat
        }.trim()
    }
}
