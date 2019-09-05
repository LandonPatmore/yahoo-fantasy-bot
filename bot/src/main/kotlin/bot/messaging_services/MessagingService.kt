package bot.messaging_services

import com.mashape.unirest.http.exceptions.UnirestException
import io.reactivex.functions.Consumer
import java.io.IOException
import java.lang.Exception

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
            sendMessage(message)
        } catch (e : Exception) {
            println(e.localizedMessage)
        }
    }

    private fun correctMessage(message: String): String {
        return when {
            message.startsWith("n") -> message.substring(1)
            message.startsWith("\\") -> message.substring(1)
            message.startsWith("\\n") -> message.substring(2)
            message.endsWith("\\") -> message.substring(0, message.length)
            message.endsWith("\\n") -> message.substring(0, message.length - 1)
            else -> message
        }.trim()
    }
}
