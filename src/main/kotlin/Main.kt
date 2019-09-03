
import bridges.CloseScoreUpdateBridge
import utils.DataRetriever
import utils.MessageSender


fun main() {
    DataRetriever.authenticate()
    MessageSender
    CloseScoreUpdateBridge.dataObserver.onNext(DataRetriever.getTeamsData())
}