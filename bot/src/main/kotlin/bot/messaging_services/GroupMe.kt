package bot.messaging_services

import com.mashape.unirest.http.Unirest
import shared.EnvVariable

private const val URL = "https://api.groupme.com/v3/bots/post"
private const val NAME = "GroupMe"
private const val MAX_MESSAGE_LENGTH = 1000

object GroupMe : MessagingService(MAX_MESSAGE_LENGTH) {
    override fun sendMessage(message: String){
        EnvVariable.Str.GroupMeBotId.variable.let {
            println("Sending $NAME message...")
            val formatted = formatMessage(message);
            val response = Unirest.post(URL)
                .header("Content-Type", "application/json")
                .body("{\"text\" : \"$formatted\", \"bot_id\" : \"$it\"}")
                .asString()
            println("Status Text: " + response.statusText + " | Status: " + response.status)
        }
    }
}

private fun formatMessage(message: String): String {
    // there isn’t markdown formatting other than > for groupme
    return message.replace("<b>", "").replace("</b>", "")
}
