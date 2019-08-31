
import io.reactivex.Observable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit



fun main() {
    val latch = CountDownLatch(1)
    setUp()
    DataRetriever.authenticate()
    Observable.interval(0, 15, TimeUnit.SECONDS)
        .subscribe {
            val event = DataRetriever.grabData(DataRetriever.BASE_URL + DataRetriever.LEAGUE_KEY + "/scoreboard")
            DataBridge.dataObserver.onNext(event)
        }
    latch.await()

//    val today = Calendar.getInstance()
//    today.set(Calendar.HOUR_OF_DAY, 2)
//    today.set(Calendar.MINUTE, 0)
//    today.set(Calendar.SECOND, 0)
//
//// every night at 2am you run your task
//    val timer = Timer()
//    timer.schedule(YourTask(), today.time, TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) // period: 1 day
}

fun setUp() {
    DataBridge.dataObservable
        .convertToSingleMatchUp()
        .convertToMatchUpObject()
        .convertToMatchUpMessage()
        .subscribe{
            println(it)
        }
}