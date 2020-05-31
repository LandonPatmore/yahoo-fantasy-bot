package bot.messaging_services

import com.mashape.unirest.http.Unirest
import shared.EnvVariable

private const val NAME = "Slack"
private const val MAX_MESSAGE_LENGTH = 40000

object Slack : MessagingService(MAX_MESSAGE_LENGTH) {
    override fun sendMessage(message: String) {
        EnvVariable.Str.SlackWebhookUrl.variable.let {
            println("Sending $NAME message...")
            val formatted = formatMessage(message);
            val response = Unirest.post(it)
                .header("Content-Type", "application/json")
                .body("{\"text\" : \"$formatted\"}")
                .asString()
            println("Status Text: " + response.statusText + " | Status: " + response.status)
        }
    }
}

private fun formatMessage(message: String): String {
    return message.replace("<b>", "*").replace("</b>", "*")
}
