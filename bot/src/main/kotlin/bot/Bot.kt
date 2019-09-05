import bot.utils.BotArbiter
import bot.utils.DataRetriever


fun main() {
    DataRetriever.authenticate()
    BotArbiter.start()
}
