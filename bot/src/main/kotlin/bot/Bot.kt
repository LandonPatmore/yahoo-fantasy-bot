@file:JvmName("Bot")
package bot

import bot.utils.Arbiter
import bot.utils.DataRetriever
import shared.EnvVariablesChecker

fun main() {
    EnvVariablesChecker.check()
    DataRetriever.authenticate()
    Arbiter.start()
}
