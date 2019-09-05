import utils.BotArbiter
import utils.DataRetriever


fun main() {
    DataRetriever.authenticate()
    BotArbiter.start()
}