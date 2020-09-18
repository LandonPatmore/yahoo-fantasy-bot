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

package com.landonpatmore.yahoofantasybot.bot.utils

import com.landonpatmore.yahoofantasybot.bot.bridges.*
import com.landonpatmore.yahoofantasybot.bot.messaging.IMessagingService
import com.landonpatmore.yahoofantasybot.bot.messaging.Message
import com.landonpatmore.yahoofantasybot.bot.transformers.*
import com.landonpatmore.yahoofantasybot.bot.utils.models.Configuration
import com.landonpatmore.yahoofantasybot.bot.utils.models.YahooApiRequest
import com.landonpatmore.yahoofantasybot.shared.database.Db
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

class Arbiter(
    private val dataRetriever: IDataRetriever,
    private val messageBridge: MessageBridge,
    private val alertsRunner: AlertsRunner,
    private val transactionsBridge: TransactionsBridge,
    private val scoreUpdateBridge: ScoreUpdateBridge,
    private val closeScoreUpdateBridge: CloseScoreUpdateBridge,
    private val standingsBridge: StandingsBridge,
    private val matchUpBridge: MatchUpBridge,
    private val configurationBridge: ConfigurationBridge,
    private val messagingServices: List<IMessagingService>,
    private val database: Db
) {

    private fun setup() {
        setupTransactionsBridge()
        setupScoreUpdateBridge()
        setupCloseScoreUpdateBridge()
        setupMatchUpBridge()
        setupStandingsBridge()
        setupMessageBridge()
        sendInitialMessage()
        alertsRunner.start()
    }

    fun start() {
        setup()

        Observable.interval(0, 15, TimeUnit.SECONDS)
            .subscribe {
                configurationBridge.consumer.accept(Configuration.Alerts(database.getAlerts()))
                try {
                    val event = dataRetriever.yahooApiRequest(YahooApiRequest.Transactions)
                    val latestTimeChecked = database.getLatestTimeChecked()
                    if (latestTimeChecked.time != -1L) {
                        transactionsBridge.consumer.accept(
                            Pair(
                                if (latestTimeChecked.time == -1L) {
                                    // we say -15 so that we can grab anything right before we got this bad value
                                    System.currentTimeMillis() - 15
                                } else {
                                    latestTimeChecked.time
                                } / 1000, event
                            )
                        )
                    }
                    database.saveLatestTimeChecked(System.currentTimeMillis())
                } catch (e: Exception) {
                    println(e.message)
                }
            }
    }

    private fun sendInitialMessage() {
        if (!database.wasStartupMessageReceived()) {
            messageBridge.consumer.accept(
                Message.Generic(
                    """
                        |Thanks for using me!  I will notify you about things happening in your league in real time!
                        |Star/fork me on Github: https://github.com/LandonPatmore/yahoo-fantasy-bot
                        |Having issues?: https://github.com/LandonPatmore/yahoo-fantasy-bot/issues
                    """.trimMargin()
                )
            )
            database.startupMessageReceived()
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
            .convertToMessageInfo()

        messagingServices.forEach {
            messages.subscribe(it)
        }
    }

//    private fun configureMessagingServices(services: List<MessagingService>) {
//        messagingDisposable.clear()
//
//        services.map {
//            if (validateMessagingService(it.service)) {
//                when (it.service) {
//                    MessagingService.DISCORD -> Discord(it.url)
//                    MessagingService.SLACK -> Slack(it.url)
//                    MessagingService.GROUP_ME -> GroupMe(it.url)
//                    else -> null
//                }
//            } else {
//                null
//            }
//        }.forEach {
//            it?.let {
//                messagingDisposable.add(messages.subscribe(it))
//            }
//        }
//    }
//
//    private fun validateMessagingService(service: Int): Boolean {
//        return when (service) {
//            MessagingService.DISCORD, MessagingService.SLACK, MessagingService.GROUP_ME -> true
//            else -> false
//        }
//    }
}
