package bot.utils

import bot.bridges.*
import io.reactivex.Observable
import bot.messaging_services.Discord
import bot.messaging_services.GroupMe
import bot.messaging_services.Message
import bot.messaging_services.Slack
import bot.transformers.*
import bot.utils.jobs.CloseScoreUpdateJob
import bot.utils.jobs.MatchUpJob
import bot.utils.jobs.ScoreUpdateJob
import bot.utils.jobs.StandingsJob
import shared.Postgres
import utils.JobRunner
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
        sendInitialMessage()
    }

    fun start() {
        Observable.interval(0, 15, TimeUnit.SECONDS)
            .subscribe {
                val event = DataRetriever.getTransactions()
                TransactionsBridge.dataObserver.onNext(event)

                Postgres.saveLastTimeChecked()
            }
    }

    private fun sendInitialMessage() {
        val startUpMessage = "Hey there! I am the Yahoo Fantasy bot that notifies you about all things happening in your league!" +
                "  Star me on Github: https://github.com/landonp1203/yahoo-fantasy-bot"
        if(!Postgres.startupMessageSent) {
            MessageBridge.dataObserver.onNext(Message.Generic(startUpMessage))
            Postgres.markStartupMessageReceived()
        } else {
            println("Start up message already sent, not sending...")
        }
    }

    private fun setupTransactionsBridge() {
        val transactions = TransactionsBridge.dataObservable
            .convertToTransactionMessage()

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
