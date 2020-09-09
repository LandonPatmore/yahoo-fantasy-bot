package bot.messaging

class MockMessageService : IMessagingService {
    override fun cleanMessage(message: String): String {
        TODO()
    }

    override fun sendMessage(message: String) {
        println(message)
    }

    override fun createMessage(message: String) {
        sendMessage(correctMessage(message))
    }

    override fun correctMessage(message: String): String {
        return message
    }

    override fun accept(message: String?) {
        message?.let { createMessage(it) }
    }
}