package bot.messaging

import com.mashape.unirest.http.Unirest
import com.mashape.unirest.request.body.RequestBodyEntity
import shared.EnvVariable

class GroupMe : MessagingService() {
    override val name = "GroupMe"

    override val maxMessageLength = 1000

    override val url = "https://api.groupme.com/v3/bots/post"

    override fun generateRequest(message: String): RequestBodyEntity =
        Unirest.post(url)
            .header("Content-Type", "application/json")
            .body("{\"text\" : \"$message\", \"bot_id\" : \"${EnvVariable.Str.GroupMeBotId.variable}\"}")

    override fun cleanMessage(message: String): String =
        message.replace("<b>", "").replace("</b>", "")
}
