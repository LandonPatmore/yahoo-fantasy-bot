package messaging_services

abstract class MessagingService(private val maxMessageLength: Int) {

    protected abstract fun sendMessage(message: String)

    fun processMessage(message: String): String {
        if (message.length > maxMessageLength) {
            
        }

        return message
    }
}
