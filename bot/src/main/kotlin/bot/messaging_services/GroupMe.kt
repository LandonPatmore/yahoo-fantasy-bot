package bot.messaging_services

import com.mashape.unirest.http.Unirest
import shared.EnvVariables

private const val URL = "https://api.groupme.com/v3/bots/post"
private const val NAME = "GroupMe"
private const val MAX_MESSAGE_LENGTH = 1000

object GroupMe : MessagingService(MAX_MESSAGE_LENGTH) {
    override fun sendMessage(message: String){
        EnvVariables.GroupMeBotId.variable?.let {
            println("Sending $NAME message...")
            val response = Unirest.post(URL)
                .header("Content-Type", "application/json")
                .body("{\"text\" : \"$message\", \"bot_id\" : \"$it\"}")
                .asJson()
            println("Status Text: " + response.statusText + " | Status: " + response.status)
        }
    }
}
