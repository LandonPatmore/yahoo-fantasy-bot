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

import com.mashape.unirest.http.Unirest
import com.mashape.unirest.request.body.RequestBodyEntity

class Discord(url: String) : MessagingService(url) {
    override val name = "Discord"

    override val maxMessageLength = 2000

    override fun generateRequest(message: String): RequestBodyEntity =
        Unirest.post(url)
            .header("Content-Type", "application/json")
            .body("{\"content\" : \"$message\"}")

    override fun cleanMessage(message: String): String = message

    override fun generateMessage(message: Pair<String, String>, title: Boolean): String {
        return if (title) {
            "${message.first}\\n>>> ${message.second}"
        } else {
            ">>> ${message.second}"
        }
    }
}
