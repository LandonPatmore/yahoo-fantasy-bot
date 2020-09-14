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

import com.landonpatmore.yahoofantasybot.backend.models.*
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.getMessagingServices() {
    get("/messagingServices") {
        call.respond(
            MessagingServices(
                listOf("discord.com/123"), listOf(
                    "groupme.com/12928dn1"
                ), listOf("slack.com/129749")
            )
        )
    }
}

fun Route.getGameKey() {
    get("/gameKey") {
        call.respond(GameKey(GameKey.NFL))
    }
}

fun Route.getLeagueId() {
    get("/leagueId") {
        call.respond(LeagueId("1983729"))
    }
}

fun Route.getAlerts() {
    get("/alerts") {
        call.respond(
            Alerts(
                listOf(
                    Alert(0, 1, 2, 3, 4, 5),
                    Alert(0, 1, 2, 3, 4, 5),
                    Alert(0, 1, 2, 3, 4, 5)
                )
            )
        )
    }
}

fun Route.getMessageType() {
    get("/messageType") {
        call.respond(MessageType(MessageType.BATCH))
    }
}

fun Route.getLatestVersion(classLoader: ClassLoader) {
    get("/latestVersion") {
        val release = HttpClient(OkHttp) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }.use {
            it.get<ReleaseInformation>(ReleaseInformation.URL)
        }.apply {
            newVersionExists = determineNewVersionExists(tag_name, classLoader)
        }
        call.respond(release)
    }
}


private fun determineNewVersionExists(
    tagName: String,
    classLoader: ClassLoader
): Boolean {
    val currentVersion =
        classLoader.getResource("VERSION")
            ?.readText()?.split(".") ?: return false
    val latestVersion = tagName.split(".")

    if (latestVersion.size > currentVersion.size) {
        return true
    } else {
        for ((i, latest) in latestVersion.withIndex()) {
            when {
                latest.toInt() == currentVersion[i].toInt() -> continue
                latest.toInt() < currentVersion[i].toInt() -> return false
                latest.toInt() > currentVersion[i].toInt() -> return true
            }
        }
        return false
    }
}

// TODO: Figure out
//fun Route.getAuth() {
//    get("/auth") {
//        // this is because of an IOException that can be thrown
//        // inside of getAccessToken()
//        @Suppress("BlockingMethodInNonBlockingContext")
//        service?.getAccessToken(call.request.queryParameters["code"])
//            ?.let {
//                Postgres().saveTokenData(it)
//            }
//    }
//}

