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

package com.landonpatmore.yahoofantasybot.backend.routes

import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import shared.database.Db
import shared.database.models.*

fun Application.putRoutes(db: Db) {
    routing {
        putMessagingServices(db)
        putGameKey(db)
        putLeagueId(db)
        putAlerts(db)
        putMessageType(db)
    }
}

private fun Route.putMessagingServices(db: Db) {
    put("/messagingServices") {
        val services = call.receiveArray<Array<MessagingService>>()
        db.saveMessagingServices(services)
        call.respond("Received!")
    }
}

private fun Route.putGameKey(db: Db) {
    put("/gameKeys") {
        val gameKey = call.receiveArray<Array<GameKey>>()
        db.saveGameKeys(gameKey)
        call.respond("Received!")
    }
}

private fun Route.putLeagueId(db: Db) {
    put("/leagueIds") {
        val leagueId = call.receiveArray<Array<LeagueId>>()
        db.saveLeagueIds(leagueId)
        call.respond("Received!")
    }
}

private fun Route.putAlerts(db: Db) {
    put("/alerts") {
        val alerts = call.receiveArray<Array<Alert>>()
        db.saveAlerts(alerts)
        call.respond("Received!")
    }
}

private fun Route.putMessageType(db: Db) {
    put("/messageType") {
        val messageType = call.receive<MessageType>()
        db.saveMessageType(messageType)
        call.respond("Received!")
    }
}

private suspend inline fun <reified T> ApplicationCall.receiveArray(): T {
    val json = this.receiveOrNull<String>()
    return Gson().fromJson(json, T::class.java)
}