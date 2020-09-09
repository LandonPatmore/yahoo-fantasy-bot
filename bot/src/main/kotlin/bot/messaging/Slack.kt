package bot.messaging

import com.mashape.unirest.http.Unirest
import com.mashape.unirest.request.body.RequestBodyEntity
import shared.EnvVariable

class Slack : MessagingService() {
    override val name = "Slack"

    override val maxMessageLength = 40000

    override val url = EnvVariable.Str.SlackWebhookUrl.variable

    override fun generateRequest(message: String): RequestBodyEntity =
        Unirest.post(url)
            .header("Content-Type", "application/json")
            .body("{\"text\" : \"$message\"}")

    override fun cleanMessage(message: String): String =
        message.replace("<b>", "*").replace("</b>", "*")
}
