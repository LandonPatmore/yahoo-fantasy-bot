package messaging_services

abstract class MessagingService {

    fun correctMessage(message: String): String {
        var correctedMessage = ""
        if (message.endsWith("\\")) {
            correctedMessage = message.substring(0, message.length - 1)
        } else if (message.startsWith("n")) {
            correctedMessage = message.substring(1)
        }
        return correctedMessage
    }
}
