
import utils.DataRetriever
import utils.MessageSender


fun main() {
    DataRetriever.authenticate()
    MessageSender.start()
}