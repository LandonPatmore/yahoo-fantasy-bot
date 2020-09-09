package bot.utils

import bot.bridges.*
import bot.messaging.*
import bot.transformers.*
import bot.utils.jobs.CloseScoreUpdateJob
import bot.utils.jobs.MatchUpJob
import bot.utils.jobs.ScoreUpdateJob
import bot.utils.jobs.StandingsJob
import bot.utils.models.YahooApiRequest
import io.reactivex.rxjava3.core.Observable
import shared.EnvVariable
import shared.IDatabase
import shared.Postgres
import java.util.concurrent.TimeUnit

class Arbiter(
    private val dataRetriever: IDataRetriever,
    private val messageBridge: MessageBridge,
    private val jobRunner: JobRunner,
    private val transactionsBridge: TransactionsBridge,
    private val scoreUpdateBridge: ScoreUpdateBridge,
    private val closeScoreUpdateBridge: CloseScoreUpdateBridge,
    private val standingsBridge: StandingsBridge,
    private val matchUpBridge: MatchUpBridge,
    private val database: IDatabase,
    private val messageServices: List<IMessagingService>
) {

    private fun setup() {
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
        setup()

        Observable.interval(0, 15, TimeUnit.SECONDS)
            .subscribe {
                try {
                    val event = dataRetriever.yahooApiRequest(YahooApiRequest.Transactions)
                    val latestTimeChecked = database.latestTimeChecked()
                    transactionsBridge.dataObserver.accept(Pair(latestTimeChecked, event))
                    database.saveLastTimeChecked()
                } catch (e: Exception) {
                    println(e.message)
                }
            }
    }

    private fun sendInitialMessage() {
        if (!database.startupMessageSent()) {
            messageBridge.dataObserver.accept(
                Message.Generic(
                    """
                        |Thanks for using me!  I will notify you about things happening in your league in real time!
                        |Star/fork me on Github: https://github.com/LandonPatmore/yahoo-fantasy-bot
                        |Having issues?: https://github.com/LandonPatmore/yahoo-fantasy-bot/issues
                    """.trimMargin()
                )
            )
            database.markStartupMessageReceived()
        } else {
            println("Start up message already sent, not sending...")
        }
    }

    private fun setupTransactionsBridge() {
        val transactions = transactionsBridge.dataObservable
            .convertToTransactionMessage()

        transactions.subscribe(messageBridge.dataObserver)
    }

    private fun setupScoreUpdateBridge() {
        val transactions = scoreUpdateBridge.dataObservable
            .convertToMatchUpObject()
            .convertToScoreUpdateMessage()

        transactions.subscribe(messageBridge.dataObserver)
    }

    private fun setupCloseScoreUpdateBridge() {
        val transactions = closeScoreUpdateBridge.dataObservable
            .convertToMatchUpObject()
            .convertToScoreUpdateMessage(true)

        transactions.subscribe(messageBridge.dataObserver)
    }

    private fun setupMatchUpBridge() {
        val transactions = matchUpBridge.dataObservable
            .convertToMatchUpObject()
            .convertToMatchUpMessage()

        transactions.subscribe(messageBridge.dataObserver)
    }

    private fun setupStandingsBridge() {
        val standings = standingsBridge.dataObservable
            .convertToStandingsObject()
            .convertToStandingsMessage()

        standings.subscribe(messageBridge.dataObserver)
    }

    private fun setupMessageBridge() {
        val messages = messageBridge.dataObservable
            .convertToStringMessage()

        messageServices.forEach {
            messages.subscribe(it)
        }

//        if (EnvVariable.Str.DiscordWebhookUrl.variable.isNotEmpty()) {
//            messages.subscribe(Discord())
//        }
//        if (EnvVariable.Str.GroupMeBotId.variable.isNotEmpty()) {
//            messages.subscribe(GroupMe())
//        }
//        if (EnvVariable.Str.SlackWebhookUrl.variable.isNotEmpty()) {
//            messages.subscribe(Slack())
//        }
    }

    private fun setupJobs() {
        // Times are in UTC since it is not effected by DST
        if (EnvVariable.Bool.OptInCloseScore.variable) {
            jobRunner.createJob(CloseScoreUpdateJob::class.java, "0 30 23 ? 9-1 MON *")
        }

        jobRunner.createJob(MatchUpJob::class.java, "0 30 23 ? 9-1 THU *")
        jobRunner.createJob(StandingsJob::class.java, "0 30 16 ? 9-1 TUE *")
        jobRunner.createJob(ScoreUpdateJob::class.java, "0 55 3 ? 9-1 FRI *")
        jobRunner.createJob(ScoreUpdateJob::class.java, "0 00 17 ? 9-1 SUN *")
        jobRunner.createJob(ScoreUpdateJob::class.java, "0 00 20 ? 9-1 SUN *")
        jobRunner.createJob(ScoreUpdateJob::class.java, "0 00 0 ? 9-1 MON *")
        jobRunner.createJob(ScoreUpdateJob::class.java, "0 55 3 ? 9-1 MON *")
        jobRunner.createJob(ScoreUpdateJob::class.java, "0 55 3 ? 9-1 TUE *")

        jobRunner.runJobs()
    }
}
