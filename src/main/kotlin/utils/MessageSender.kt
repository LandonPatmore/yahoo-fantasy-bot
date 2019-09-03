package utils

import bridges.CloseScoreUpdateBridge
import bridges.MatchUpBridge
import bridges.ScoreUpdateBridge
import bridges.TransactionsBridge
import io.reactivex.Observable
import messaging_services.Discord
import messaging_services.GroupMe
import messaging_services.Slack
import transformers.convertToMatchUpMessage
import transformers.convertToMatchUpObject
import transformers.convertToScoreUpdateMessage
import transformers.convertToTransactionMessage
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object MessageSender {
    init {
        setupTransactionsBridge()
        setupScoreUpdateBridge()
        setupCloseScoreUpdateBridge()
        setupMatchUpBridge()
        setupScheduledUpdates()
    }

    fun start() {
        val latch = CountDownLatch(1)
        Observable.interval(0, 15, TimeUnit.SECONDS)
            .subscribe {
                val event = DataRetriever.getTransactions()
                TransactionsBridge.dataObserver.onNext(event)
            }
        latch.await()
    }

    private fun setupTransactionsBridge() {
        val transactions = TransactionsBridge.dataObservable
            .convertToTransactionMessage(1000)

        transactions.subscribe(Discord)
        transactions.subscribe(Slack)
        transactions.subscribe(GroupMe)
    }

    private fun setupScoreUpdateBridge() {
        val transactions = ScoreUpdateBridge.dataObservable
            .convertToMatchUpObject()
            .convertToScoreUpdateMessage()

        transactions.subscribe(Discord)
        transactions.subscribe(Slack)
        transactions.subscribe(GroupMe)
    }

    private fun setupCloseScoreUpdateBridge() {
        val transactions = CloseScoreUpdateBridge.dataObservable
            .convertToMatchUpObject()
            .convertToScoreUpdateMessage(true)

        transactions.subscribe(Discord)
        transactions.subscribe(Slack)
        transactions.subscribe(GroupMe)
    }

    private fun setupMatchUpBridge() {
        val transactions = MatchUpBridge.dataObservable
            .convertToMatchUpObject()
            .convertToMatchUpMessage()

        transactions.subscribe(Discord)
        transactions.subscribe(Slack)
        transactions.subscribe(GroupMe)
    }

    private fun setupScheduledUpdates() {
//        UpdateCreator.createUpdate(
//            "Weekly match up update",
//            19,
//            30,
//            Calendar.THURSDAY,
//            UpdateCreator.TaskType.MatchUpUpdate
//        )
//        UpdateCreator.createUpdate(
//            "Thursday score update",
//            23,
//            55,
//            Calendar.THURSDAY,
//            UpdateCreator.TaskType.ScoreUpdate
//        )
//        UpdateCreator.createUpdate("Sunday score update", 12, 30, Calendar.SUNDAY, UpdateCreator.TaskType.ScoreUpdate)
//        UpdateCreator.createUpdate("Sunday score update", 16, 0, Calendar.SUNDAY, UpdateCreator.TaskType.ScoreUpdate)
//        UpdateCreator.createUpdate("Sunday score update", 20, 0, Calendar.SUNDAY, UpdateCreator.TaskType.ScoreUpdate)
//        UpdateCreator.createUpdate("Sunday score update", 23, 55, Calendar.SUNDAY, UpdateCreator.TaskType.ScoreUpdate)
//        UpdateCreator.createUpdate("Monday score update", 23, 55, Calendar.MONDAY, UpdateCreator.TaskType.ScoreUpdate)
//        UpdateCreator.createUpdate(
//            "Close score update",
//            18,
//            30,
//            Calendar.MONDAY,
//            UpdateCreator.TaskType.CloseScoreUpdate
//        )
    }
}