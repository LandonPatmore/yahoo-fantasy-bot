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

package com.landonpatmore.yahoofantasybot.bot.messaging

import com.mashape.unirest.http.exceptions.UnirestException
import com.mashape.unirest.request.body.RequestBodyEntity

abstract class MessagingService(protected val url: String) : IMessagingService {

    protected abstract val name: String

    protected abstract val maxMessageLength: Int

    protected abstract fun generateRequest(message: String): RequestBodyEntity

    override fun accept(message: Pair<String, String>?) {
        if (url.isNotEmpty()) {
            // message should never be null, this is just a safety check
            message?.let { createMessage(it) }
        }
    }

    @Throws(UnirestException::class)
    override fun sendMessage(message: String) {
        val response = generateRequest(cleanMessage(message)).asJson()
        println("$name status code: ${response.status}")
    }

    override fun createMessage(messageInfo: Pair<String, String>, title: Boolean) {
        try {
            // TODO: Remove this sleep
            val message = generateMessage(messageInfo, title)
            Thread.sleep(1000)
            if (message.length > maxMessageLength) {
                val subMessage = message.substring(0, maxMessageLength + 1)
                sendMessage(correctMessage(subMessage))
                createMessage(
                    Pair(messageInfo.first, message.substring(maxMessageLength + 1)),
                    false
                )
            }
            sendMessage(correctMessage(message))
        } catch (e: Exception) {
            println(e.message)
        }
    }

    override fun correctMessage(message: String): String {
        val properJsonFormat = message.replace("\n", "\\n")

        return when {
            properJsonFormat.startsWith("n") -> properJsonFormat.substring(1)
            properJsonFormat.startsWith("\\") -> properJsonFormat.substring(1)
            properJsonFormat.startsWith("\\n") -> properJsonFormat.substring(2)
            properJsonFormat.endsWith("\\") -> properJsonFormat.substring(
                0,
                message.length
            )
            properJsonFormat.endsWith("\\n") -> properJsonFormat.substring(
                0,
                message.length - 1
            )
            else -> properJsonFormat
        }.trim()
    }

    override fun generateMessage(message: Pair<String, String>, title: Boolean): String {
        return if (title) {
            "${message.first}\\n${message.second}"
        } else {
            message.second
        }
    }
}
