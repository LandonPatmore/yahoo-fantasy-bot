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
import com.landonpatmore.yahoofantasybot.shared.database.Db
import com.landonpatmore.yahoofantasybot.shared.database.models.Alert
import com.landonpatmore.yahoofantasybot.shared.database.models.League
import com.landonpatmore.yahoofantasybot.shared.database.models.MessageType
import com.landonpatmore.yahoofantasybot.shared.database.models.MessagingService
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.putRoutes(db: Db) {
    routing {
        putMessagingServices(db)
        putLeagues(db)
        putAlerts(db)
        putMessageType(db)
    }
}

private fun Route.putMessagingServices(db: Db) {
    put("/messagingServices") {
        val services = call.receiveJson<Array<MessagingService>>()
        db.saveMessagingServices(services)
        call.respond(db.getMessagingServices())
    }
}

private fun Route.putLeagues(db: Db) {
    put("/leagues") {
        val leagues = call.receiveJson<Array<League>>()
        db.saveLeagues(leagues)
        call.respond(db.getLeagues())
    }
}

private fun Route.putAlerts(db: Db) {
    put("/alerts") {
        val alerts = call.receiveJson<Array<Alert>>()
        db.saveAlerts(alerts)
        call.respond(db.getAlerts())
    }
}

private fun Route.putMessageType(db: Db) {
    put("/messageType") {
        val messageType = call.receiveJson<MessageType>()
        db.saveMessageType(messageType)
        call.respond(db.getMessageType())
    }
}

private suspend inline fun <reified T> ApplicationCall.receiveJson(): T {
    val json = this.receiveOrNull<String>()
    return Gson().fromJson(json, T::class.java)
}