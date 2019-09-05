@file:JvmName("Bot")
package bot

import bot.utils.Arbiter
import bot.utils.DataRetriever

fun main() {
    DataRetriever.authenticate()
    Arbiter.start()
}
