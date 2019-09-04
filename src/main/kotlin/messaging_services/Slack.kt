package messaging_services

import com.mashape.unirest.http.Unirest
import utils.EnvVariables

private const val MAX_MESSAGE_LENGTH = 40000

object Slack : MessagingService(MAX_MESSAGE_LENGTH) {
    override fun sendMessage(message: String) {
        EnvVariables.SlackWebhookUrl.variable?.let {
            val response = Unirest.post(it)
                .header("Content-Type", "application/json")
                .body("{\"text\" : \"```$message```\"}")
                .asString()
            println("Status Text: " + response.statusText + " | Status: " + response.status)
        }
    }
}