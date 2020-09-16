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

sealed class Message(val title: String, val message: String) {
    class Score(message: String) : Message("Score", message)
    class CloseScore(message: String) : Message("Close Score", message)
    class MatchUp(message: String) : Message("Match Up", message)
    class Standings(message: String) : Message("Standings", message)
    class Generic(message: String) : Message("Message", message)
    class Unknown(message: String) : Message("", message)
    sealed class Transaction(title: String, message: String) :
        Message(title, message) {
        class Add(message: String) : Transaction("Add", message)
        class Drop(message: String) : Transaction("Drop", message)
        class AddDrop(message: String) : Transaction("Add/Drop", message)
        class Trade(message: String) : Transaction("Trade", message)
        class Commish(message: String) : Transaction("Commish", message)
    }
}
