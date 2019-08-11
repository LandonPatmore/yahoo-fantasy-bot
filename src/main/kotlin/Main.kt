import java.util.concurrent.CountDownLatch

fun main() {
    val latch = CountDownLatch(1)
    DataBridge.dataObservable
        .toTransaction()
        .toMessage()
        .subscribe{
            println(it)
        }
    DataRetriever.grabData(DataRetriever.BASE_URL + DataRetriever.LEAGUE_KEY + "/transactions")
    latch.await()
}