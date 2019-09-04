package utils

import bridges.*
import io.reactivex.Observable
import messaging_services.Discord
import messaging_services.GroupMe
import messaging_services.Slack
import transformers.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object MessageSender {
    init {
        setupTransactionsBridge()
        setupScoreUpdateBridge()
        setupCloseScoreUpdateBridge()
        setupMatchUpBridge()
        setupStandingsBridge()
        setupScheduledUpdates()

        setupMessageBridge()
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

        transactions.subscribe(MessageBridge.dataObserver)
    }

    private fun setupScoreUpdateBridge() {
        val transactions = ScoreUpdateBridge.dataObservable
            .convertToMatchUpObject()
            .convertToScoreUpdateMessage()

        transactions.subscribe(MessageBridge.dataObserver)
    }

    private fun setupCloseScoreUpdateBridge() {
        val transactions = CloseScoreUpdateBridge.dataObservable
            .convertToMatchUpObject()
            .convertToScoreUpdateMessage(true)

        transactions.subscribe(MessageBridge.dataObserver)
    }

    private fun setupMatchUpBridge() {
        val transactions = MatchUpBridge.dataObservable
            .convertToMatchUpObject()
            .convertToMatchUpMessage()

        transactions.subscribe(MessageBridge.dataObserver)
    }

    private fun setupStandingsBridge() {
        val standings = StandingsBridge.dataObservable
            .convertToStandingsObject()
            .convertToStandingsMessage()

        standings.subscribe(MessageBridge.dataObserver)
    }

    private fun setupMessageBridge() {
        val messages = MessageBridge.dataObservable
            .convertToStringMessage()

        messages.subscribe(Discord)
        messages.subscribe(GroupMe)
        messages.subscribe(Slack)
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