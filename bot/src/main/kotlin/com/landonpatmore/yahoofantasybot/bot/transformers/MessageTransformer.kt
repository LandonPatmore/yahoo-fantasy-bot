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

package com.landonpatmore.yahoofantasybot.bot.transformers

import com.landonpatmore.yahoofantasybot.bot.messaging.Message
import com.landonpatmore.yahoofantasybot.bot.utils.bold
import io.reactivex.rxjava3.core.Observable

fun Observable<Message>.convertToMessageInfo(): Observable<Pair<String, String>> =
    filter {
        it !is Message.Unknown
    }.map {
        Pair(createTitle(it.title), it.message)
    }.filter {
        it.second.isNotEmpty()
    }

private fun createTitle(title: String): String {
    return "\uD83D\uDCE3 ${("$title Alert").toUpperCase().bold()}\\n━━━━━━━━━"
}

private fun createMessage(alertTitle: String, message: String): Pair<String, String> {
    return Pair(alertTitle, message)
}
