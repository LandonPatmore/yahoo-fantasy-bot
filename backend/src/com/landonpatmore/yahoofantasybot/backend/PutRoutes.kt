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

package com.landonpatmore.yahoofantasybot.backend

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import shared.database.models.*

fun Route.putMessagingServices() {
    put("/messagingServices") {
        val services = call.receive<MessagingServices>()
        println(services)
        call.respond("Received!")
    }
}

fun Route.putGameKey() {
    put("/gameKey") {
        val gameKey = call.receive<GameKey>()
        println(gameKey)
        call.respond("Received!")
    }
}

fun Route.putLeagueId() {
    put("/leagueId") {
        val leagueId = call.receive<LeagueId>()
        println(leagueId)
        call.respond("Received!")
    }
}

fun Route.putAlerts() {
    put("/alerts") {
        val alerts = call.receive<Alerts>()
        println(alerts)
        call.respond("Received!")
    }
}

fun Route.putMessageType() {
    put("/messageType") {
        val messageType = call.receive<MessageType>()
        println(messageType)
        call.respond("Received!")
    }
}