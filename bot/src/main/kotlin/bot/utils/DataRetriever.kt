package bot.utils

import com.github.scribejava.apis.YahooApi20
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.model.OAuthConstants
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import shared.EnvVariables
import shared.Postgres

object DataRetriever {
    private const val SCOREBOARD = "/scoreboard"
    private const val STANDINGS = "/standings"
    private const val TRANSACTIONS = "/transactions"

    private val BASE_URL =
        "https://fantasysports.yahooapis.com/fantasy/v2/league/nfl.l.${EnvVariables.YahooLeagueId.variable}"

    private val oauthService = ServiceBuilder(EnvVariables.YahooClientId.variable)
        .apiSecret(EnvVariables.YahooClientSecret.variable)
        .callback(OAuthConstants.OOB)
        .build(YahooApi20.instance())
    private var currentToken: Pair<Long, OAuth2AccessToken>? = null


    /**
     * Checks to see whether or not a token is expired.
     *
     * @param retrieved when the token was initially retrieved
     * @param expiresIn when the token expires
     * @return if the token is expired
     */
    private fun isTokenExpired(retrieved: Long, expiresIn: Int): Boolean {
        val timeElapsed = ((System.currentTimeMillis() - retrieved) / 1000)
        return timeElapsed >= expiresIn
    }

    /**
     * Refreshes an expired token.
     */
    private fun refreshExpiredToken() {
        currentToken?.let {
            if (isTokenExpired(it.first, it.second.expiresIn)) {
                val refreshToken = oauthService.refreshAccessToken(it.second.refreshToken)
                currentToken = Pair(System.currentTimeMillis(), refreshToken)
                Postgres.saveTokenData(refreshToken)
            }
        }
    }

    fun authenticate() {
        while (true) {
            currentToken = Postgres.latestTokenData

            if (currentToken == null) { // This will run only if there is no data in the database
                println("There is currently no token data in the database.  Please authenticate with Yahoo.")
            } else {
                return
            }

            Thread.sleep(5000)
        }
    }

    private fun grabData(url: String): Document {
        refreshExpiredToken()
        println("Grabbing Data...")
        val request = OAuthRequest(Verb.GET, url)
        oauthService.signRequest(currentToken?.second, request)
        val response = oauthService.execute(request)
        println("Data grabbed.")
        return Jsoup.parse(response.body, "", Parser.xmlParser())
    }

    fun getTransactions(): Document {
        return grabData(BASE_URL + TRANSACTIONS)
    }

    fun getStandings(): Document {
        return grabData(BASE_URL + STANDINGS)
    }

    fun getTeamsData(): Document {
        return grabData(BASE_URL + SCOREBOARD)
    }
}
