@file:JvmName("Server")
package webserver

import com.github.scribejava.apis.YahooApi20
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.oauth.OAuth20Service
import shared.EnvVariables
import shared.Postgres

import spark.Spark.*

object Server {
    private var service: OAuth20Service? = null

    /**
     * Get the assigned port on Heroku
     *
     * @return int port
     */
    private val herokuAssignedPort: Int
        get() {
            return EnvVariables.Port.variable?.toInt() ?: 4567
        }

    @JvmStatic
    fun main(args: Array<String>) {
        port(herokuAssignedPort)

        registerRoutes()
    }

    private fun registerRoutes() {
        get("/") { req, res ->
            if (Postgres.latestTokenData == null) {
                println("User is not authenticated.  Sending to Yahoo.")
                res.redirect(authenticationUrl(req.scheme() + "://" + req.host()))
            } else {
                println("User is already authenticated.  Not sending to Yahoo.")
                return@get "You are already authenticated with Yahoo's servers."
            }
        }

        get("/auth") { req, _ ->
            Postgres.saveTokenData(service!!.getAccessToken(req.queryParams("code")))
            println("Access token received.  Authorized successfully.")
            "You are authorized"
        }
    }

    /**
     * Gets the authentication url from Yahoo
     *
     * @param url the callback url
     * @return String url
     */
    private fun authenticationUrl(url: String): String {
        println("Initial authorization...")

        service = ServiceBuilder(EnvVariables.YahooClientId.variable)
            .apiSecret(EnvVariables.YahooClientSecret.variable)
            .callback("$url/auth")
            .build(YahooApi20.instance())

        return service!!.authorizationUrl
    }
}
