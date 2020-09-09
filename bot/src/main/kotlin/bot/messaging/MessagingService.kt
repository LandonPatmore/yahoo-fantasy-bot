package bot.messaging

import com.mashape.unirest.http.exceptions.UnirestException
import com.mashape.unirest.request.body.RequestBodyEntity
import io.reactivex.rxjava3.functions.Consumer

abstract class MessagingService : IMessagingService {

    protected abstract val name: String

    protected abstract val maxMessageLength: Int

    protected abstract val url: String

    protected abstract fun generateRequest(message: String): RequestBodyEntity

    override fun accept(message: String?) {
        // message should never be null, this is just a safety check
        message?.let { createMessage(it) }
    }

    @Throws(UnirestException::class)
    override fun sendMessage(message: String) {
        val response = generateRequest(cleanMessage(message)).asJson()
        println("$name status code: ${response.status}")
    }

    override fun createMessage(message: String) {
        try {
            // TODO: Remove this sleep
            Thread.sleep(1000)
            if (message.length > maxMessageLength) {
                val subMessage = message.substring(0, maxMessageLength + 1)
                sendMessage(correctMessage(subMessage))
                createMessage(message.substring(maxMessageLength + 1))
            }
            sendMessage(correctMessage(message))
        } catch (e: Exception) {
            println(e.message)
        }
    }

    override fun correctMessage(message: String): String {
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
