import bridges.DataBridge
import io.reactivex.Observable
import messaging_services.Discord
import messaging_services.GroupMe
import messaging_services.Slack
import transformers.convertToMatchUpMessage
import transformers.convertToMatchUpObject
import transformers.convertToSingleMatchUp
import utils.DataRetriever
import utils.UpdateCreator
import java.util.*
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

    val timer = Timer()
    timer.schedule(
        UpdateCreator.createUpdate("Weekly Match Up Update") { println("NICE") },
        UpdateCreator.createTime(19, 30, Calendar.THURSDAY, TimeZone.getTimeZone("EST")),
        TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS)
    )
}

fun setUp() {
    val t = DataBridge.dataObservable
        .convertToSingleMatchUp()
        .convertToMatchUpObject()
        .convertToMatchUpMessage()

    t.subscribe(Discord)
    t.subscribe(Slack)
    t.subscribe(GroupMe)
}