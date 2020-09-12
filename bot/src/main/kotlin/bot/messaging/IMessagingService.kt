package bot.messaging

import io.reactivex.rxjava3.functions.Consumer

interface IMessagingService : Consumer<String> {
    @Deprecated(
        "To be removed soon",
        ReplaceWith("correctMessage()"),
        DeprecationLevel.WARNING
    )
    fun cleanMessage(message: String): String

    fun sendMessage(message: String)

    fun createMessage(message: String)

    fun correctMessage(message: String): String
}