/*
 * MIT License
 *
 * Copyright (c) 2020 Landon Patmore
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bot.utils

import bot.bridges.*
import bot.messaging.IMessagingService
import bot.messaging.Message
import bot.transformers.*
import bot.utils.jobs.CloseScoreUpdateJob
import bot.utils.jobs.MatchUpJob
import bot.utils.jobs.ScoreUpdateJob
import bot.utils.jobs.StandingsJob
import bot.utils.models.YahooApiRequest
import io.reactivex.rxjava3.core.Observable
import shared.IDatabase
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
                    val event =
                        dataRetriever.yahooApiRequest(YahooApiRequest.Transactions)
                    val latestTimeChecked = database.latestTimeChecked()
                    transactionsBridge.consumer.accept(
                        Pair(
                            latestTimeChecked,
                            event
                        )
                    )
                    database.saveLastTimeChecked()
                } catch (e: Exception) {
                    println(e.message)
                }
            }
    }

    private fun sendInitialMessage() {
        if (!database.startupMessageSent()) {
            messageBridge.consumer.accept(
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
        val transactions = transactionsBridge.eventStream
            .convertToTransactionMessage()

        transactions.subscribe(messageBridge.consumer)
    }

    private fun setupScoreUpdateBridge() {
        val transactions = scoreUpdateBridge.eventStream
            .convertToMatchUpObject()
            .convertToScoreUpdateMessage()

        transactions.subscribe(messageBridge.consumer)
    }

    private fun setupCloseScoreUpdateBridge() {
        val transactions = closeScoreUpdateBridge.eventStream
            .convertToMatchUpObject()
            .convertToScoreUpdateMessage(true)

        transactions.subscribe(messageBridge.consumer)
    }

    private fun setupMatchUpBridge() {
        val transactions = matchUpBridge.eventStream
            .convertToMatchUpObject()
            .convertToMatchUpMessage()

        transactions.subscribe(messageBridge.consumer)
    }

    private fun setupStandingsBridge() {
        val standings = standingsBridge.eventStream
            .convertToStandingsMessage()

        standings.subscribe(messageBridge.consumer)
    }

    private fun setupMessageBridge() {
        val messages = messageBridge.eventStream
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
        if (TODO()) {
            jobRunner.createJob(
                CloseScoreUpdateJob::class.java,
                "0 30 23 ? 9-1 MON *"
            )
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
