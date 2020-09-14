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

import com.landonpatmore.yahoofantasybot.backend.models.ReleaseInformation
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
            mapOf(
                "Discord" to "https://www.disocord.com/webhook/812837ahjsd",
                "GroupMe" to "https://www.groupme.com/bots/v3/asuh238s",
                "Slack" to "https://www.slack.com/webhooks/18237489172"
            )
        )
    }
}

fun Route.getGameKey() {
    get("/gameKey") {
        call.respond(mapOf("gameKey" to "NFL"))
    }
}

fun Route.getLeagueId() {
    get("/leagueId") {
        call.respond(mapOf("leagueId" to "17264628"))
    }
}

fun Route.getAlerts() {
    get("/alerts") {
        call.respond(
            mapOf(
                "Scores" to listOf("23:31", "11:23", "01:21"),
                "Standings" to listOf("23:31", "11:23", "01:21"),
                "Matchups" to listOf("23:31", "11:23", "01:21")
            )
        )
    }
}

fun Route.getJdbcUrl() {
    get("/jdbcUrl") {
        call.respond(
            mapOf(
                "jdbcUrl" to "jdbc:heroku.12747.sshuw" +
                        ".com/1827893"
            )
        )
    }
}

fun Route.getLatestVersion(classLoader: ClassLoader) {
    get("/latestVersion") {
        val release = HttpClient(OkHttp) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }.use {
            val release = it.get<ReleaseInformation>(
                "https://api.github" +
                        ".com/repos/LandonPatmore/yahoo-fantasy-bot/releases/latest"
            )
            release
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

