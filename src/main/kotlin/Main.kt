import io.reactivex.Observable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun main() {
    val latch = CountDownLatch(1)
    setUp()
    DataRetriever.authenticate()
    Observable.interval(0, 15, TimeUnit.SECONDS)
        .subscribe {
            val event = DataRetriever.grabData(DataRetriever.BASE_URL + DataRetriever.LEAGUE_KEY + "/transactions")
            DataBridge.dataObserver.onNext(event)
        }
    latch.await()
}

fun setUp() {
    DataBridge.dataObservable
        .convertToSingleTransaction()
        .convertToSendableMessage()
        .subscribe(MessageBridge.dataObserver)
}