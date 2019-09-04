package messaging_services

import com.mashape.unirest.http.Unirest
import utils.EnvVariables

private const val MAX_MESSAGE_LENGTH = 2000

object Discord : MessagingService(MAX_MESSAGE_LENGTH) {
    override fun sendMessage(message: String) {
        EnvVariables.DiscordWebhookUrl.variable?.let {
            val response =
                Unirest.post(it)
                    .header("Content-Type", "application/json")
                    .body("{\"content\" : \"```$message```\"}")
                    .asJson()
            println("Status Text: " + response.statusText + " | Status: " + response.status)
        }
    }
}