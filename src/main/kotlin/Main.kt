
import bridges.StandingsBridge
import utils.DataRetriever
import utils.MessageSender


fun main() {
    DataRetriever.authenticate()
    MessageSender
    StandingsBridge.dataObserver.onNext(DataRetriever.getStandings())
}