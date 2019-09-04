package utils

import bridges.*
import io.reactivex.Observable
import messaging_services.Discord
import messaging_services.GroupMe
import messaging_services.Slack
import transformers.*
import utils.jobs.CloseScoreUpdateJob
import utils.jobs.MatchUpJob
import utils.jobs.ScoreUpdateJob
import utils.jobs.StandingsJob
import java.util.concurrent.TimeUnit

object BotArbiter {
    init {
        setupTransactionsBridge()
        setupScoreUpdateBridge()
        setupCloseScoreUpdateBridge()
        setupMatchUpBridge()
        setupStandingsBridge()
        setupJobs()

        setupMessageBridge()
    }

    fun start() {
        Observable.interval(0, 15, TimeUnit.SECONDS)
            .subscribe {
                val event = DataRetriever.getTransactions()
                TransactionsBridge.dataObserver.onNext(event)
            }
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

    private fun setupJobs() {
        JobRunner.createJob(CloseScoreUpdateJob::class.java, "0 30 19 ? 9-1 MON *")
        JobRunner.createJob(MatchUpJob::class.java, "0 30 19 ? 9-1 THU *")
        JobRunner.createJob(StandingsJob::class.java, "0 30 12 ? 9-1 TUE *")

        JobRunner.createJob(ScoreUpdateJob::class.java, "0 55 23 ? 9-1 THU *")
        JobRunner.createJob(ScoreUpdateJob::class.java, "0 00 16 ? 9-1 SUN *")
        JobRunner.createJob(ScoreUpdateJob::class.java, "0 00 20 ? 9-1 SUN *")
        JobRunner.createJob(ScoreUpdateJob::class.java, "0 55 23 ? 9-1 SUN *")
        JobRunner.createJob(ScoreUpdateJob::class.java, "0 55 23 ? 9-1 MON *")

        JobRunner.runJobs()
    }
}