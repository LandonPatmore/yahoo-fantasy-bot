@file:JvmName("Server")

package webserver

import com.github.scribejava.apis.YahooApi20
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.oauth.OAuth20Service
import modules.sharedModule
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import shared.EnvVariable
import shared.EnvVariablesChecker
import shared.Postgres
import spark.Response
import spark.Spark.*
import webserver.exceptions.AuthenticationException
import webserver.exceptions.PostgresException


class Server : KoinComponent {
    private var service: OAuth20Service? = null
    private val envVariablesChecker: EnvVariablesChecker by inject()
    private val postgres: Postgres by inject()

    /**
     * Get the assigned port on Heroku
     *
     * @return int port
     */
    private val herokuAssignedPort: Int
        get() {
            return EnvVariable.Integer.Port.variable
        }

    fun main(args: Array<String>) {
        startKoin {
            modules(sharedModule)
        }

        envVariablesChecker.check()

        port(herokuAssignedPort)
        registerRoutes()
    }

    private fun registerRoutes() {
        get("/") { req, res ->
            if (postgres.latestTokenData() == null) {
                println("User is not authenticated.  Sending to Yahoo.")
                authenticationUrl(req.scheme() + "://" + req.host())?.let {
                    res.redirect(it)
                } ?: throw AuthenticationException()
            } else {
                println("User is already authenticated.  Not sending to Yahoo.")
                return@get "You are already authenticated with Yahoo's servers."
            }
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

        service = ServiceBuilder(EnvVariable.Str.YahooClientId.variable)
            .apiSecret(EnvVariable.Str.YahooClientSecret.variable)
            .callback("$url/auth")
            .build(YahooApi20.instance())

        return service?.authorizationUrl
    }
}
