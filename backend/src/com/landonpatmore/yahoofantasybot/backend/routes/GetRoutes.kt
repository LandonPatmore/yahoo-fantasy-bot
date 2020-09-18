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

import com.github.scribejava.apis.YahooApi20
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.oauth.OAuth20Service
import com.landonpatmore.yahoofantasybot.backend.models.Authentication
import com.landonpatmore.yahoofantasybot.backend.models.ReleaseInformation
import com.landonpatmore.yahoofantasybot.shared.database.Db
import com.landonpatmore.yahoofantasybot.shared.utils.models.EnvVariable
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*

private var service: OAuth20Service? = null

fun Application.getRoutes(db: Db, currentVersion: String?) {
    routing {
        getMessagingServices(db)
        getLeagues(db)
        getAlerts(db)
        checkAuth(db)
        getMessageType(db)
        getReleaseInformation(currentVersion)
        authenticate(db)
        auth(db)
    }
}

private fun Route.getMessagingServices(db: Db) {
    get("/messagingServices") {
        call.respond(db.getMessagingServices())
    }
}

private fun Route.getLeagues(db: Db) {
    get("/leagues") {
        call.respond(db.getLeagues())
    }
}

private fun Route.getAlerts(db: Db) {
    get("/alerts") {
        call.respond(db.getAlerts())
    }
}

private fun Route.getMessageType(db: Db) {
    get("/messageType") {
        call.respond(db.getMessageType())
    }
}

private fun Route.getReleaseInformation(currentVersion: String?) {
    get("/releaseInformation") {
        val release = HttpClient(OkHttp) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }.use {
            it.get<ReleaseInformation>(ReleaseInformation.URL)
        }.apply {
            this.currentVersion = currentVersion
            upgrade = versionChecker(currentVersion, latestVersion)
            if (!upgrade) {
                changelog = null
            }
        }
        call.respond(release)
    }
}

fun Route.authenticate(db: Db) {
    get("/authenticate") {
        if (db.getLatestTokenData() == null) {
            authenticationUrl("https://${call.request.origin.host}")?.let {
                call.respondRedirect(it)
            }
        } else {
            call.respondRedirect("/")
        }
    }
}

fun Route.checkAuth(db: Db) {
    get("/checkAuth") {
        if (db.getLatestTokenData() == null) {
            call.respond(Authentication(false))
        } else {
            call.respond(Authentication(true))
        }
    }
}

fun Route.auth(db: Db) {
    get("/auth") {
        @Suppress("BlockingMethodInNonBlockingContext")
        service?.getAccessToken(call.request.queryParameters["code"])?.let {
            db.saveToken(it)
        }
        call.respondRedirect("/")
    }
}

private fun versionChecker(
    currentVersion: String?,
    tagName: String
): Boolean {
    if (currentVersion == null) {
        return false // since we cannot determine our current version for some reason
    } else {
        val currentVersionSplit = currentVersion.split(".")
        val latestVersion = tagName.split(".")

        if (latestVersion.size > currentVersionSplit.size) {
            return true
        } else {
            for ((i, latest) in latestVersion.withIndex()) {
                when {
                    latest.toInt() == currentVersionSplit[i].toInt() -> continue
                    latest.toInt() < currentVersionSplit[i].toInt() -> return false
                    latest.toInt() > currentVersionSplit[i].toInt() -> return true
                }
            }
            return false
        }
    }
}

private fun authenticationUrl(url: String): String? {
    service = ServiceBuilder(EnvVariable.Str.YahooClientId.variable)
        .apiSecret(EnvVariable.Str.YahooClientSecret.variable)
        .callback("$url/auth")
        .build(YahooApi20.instance())

    return service?.authorizationUrl
}

