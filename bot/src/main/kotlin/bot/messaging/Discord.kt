package bot.messaging

import com.mashape.unirest.http.Unirest
import com.mashape.unirest.request.HttpRequestWithBody
import com.mashape.unirest.request.body.RequestBodyEntity
import shared.EnvVariable

class Discord : MessagingService() {
    override val name = "Discord"

    override val maxMessageLength = 2000

    override val url = EnvVariable.Str.DiscordWebhookUrl.variable

    override fun generateRequest(message: String): RequestBodyEntity =
        Unirest.post(url)
            .header("Content-Type", "application/json")
            .body("{\"content\" : \"$message\"}")

    override fun cleanMessage(message: String): String =
        message.replace("<b>", "**").replace("</b>", "**")
}
