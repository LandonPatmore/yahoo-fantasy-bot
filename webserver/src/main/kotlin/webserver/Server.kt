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

@file:JvmName("Server")

package webserver

import com.github.scribejava.apis.YahooApi20
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.oauth.OAuth20Service
import org.koin.core.KoinComponent
import org.koin.core.inject
import shared.Postgres
import spark.Response
import spark.Spark.*
import webserver.exceptions.AuthenticationException
import webserver.exceptions.PostgresException


object Server : KoinComponent {
    private var service: OAuth20Service? = null
    private val postgres: Postgres by inject()

    /**
     * Get the assigned port on Heroku
     *
     * @return int port
     */
    private val herokuAssignedPort: Int
        get() {
            return TODO()
        }

    @JvmStatic
    fun main(args: Array<String>) {
//        startKoin {
//            modules(sharedModule)
//        }

//        envVariablesChecker.check()

        port(8457)
        staticFileLocation("/webapp")
        registerRoutes()
    }

    private fun registerRoutes() {
        get("/") { req, res ->
            "hi"
//            if (postgres.latestTokenData() == null) {
//                println("User is not authenticated.  Sending to Yahoo.")
//                authenticationUrl(req.scheme() + "://" + req.host())?.let {
//                    res.redirect(it)
//                } ?: throw AuthenticationException()
//            } else {
//                println("User is already authenticated.  Not sending to Yahoo.")
//                return@get "You are already authenticated with Yahoo's servers."
//            }
        }

        get("/auth") { req, _ ->
            service?.getAccessToken(req.queryParams("code"))?.let {
                postgres.saveTokenData(it)
            } ?: throw PostgresException()
            println("Access token received.  Authorized successfully.")
            "You are authorized"
        }

        exception(PostgresException::class.java) { e, _, response: Response ->
            exceptionHandler(response, 503, e.message)
        }

        exception(AuthenticationException::class.java) { e, _, response: Response ->
            exceptionHandler(response, 401, e.message)
        }
    }

    private fun exceptionHandler(
        response: Response,
        statusCode: Int,
        message: String
    ): Response {
        return response.apply {
            status(statusCode)
            body(message)
        }
    }

    /**
     * Gets the authentication url from Yahoo
     *
     * @param url the callback url
     * @return String url
     */
    private fun authenticationUrl(url: String): String? {
        println("Initial authorization...")

        service = ServiceBuilder(TODO())
            .apiSecret(TODO())
            .callback("$url/auth")
            .build(YahooApi20.instance())

        return service?.authorizationUrl
    }
}
